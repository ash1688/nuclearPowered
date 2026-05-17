package com.ash1688.nuclearpowered.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for single-block machines.
 *
 * Provides:
 *  - Generic ItemStackHandler with configurable input/output slot counts (input slots come first)
 *  - FE energy buffer (insert-only from outside)
 *  - Tick-driven recipe progress with energy consumption
 *  - Capability exposure:
 *      TOP    -> input slots (insert only)
 *      BOTTOM -> output slots (extract only)
 *      SIDES  -> energy (FE) input
 *      FRONT  -> no I/O (player-facing GUI side)
 *  - NBT save/load for inventory, energy, progress
 *  - Container interop for vanilla menu auto-sync via SimpleContainer view
 *
 * Subclasses implement {@link #tryStartRecipe()} and {@link #completeRecipe()} for the
 * machine-specific recipe logic. The base class drives the timer.
 */
public abstract class AbstractMachineBlockEntity extends BlockEntity {
    public static final int DEFAULT_ENERGY_CAPACITY = 10_000;
    public static final int DEFAULT_ENERGY_TRANSFER = 200;

    protected final int inputSlots;
    protected final int outputSlots;
    protected final int totalSlots;

    protected final ItemStackHandler inventory;
    /** Top face: insert into input slots only. */
    protected final LazyOptional<IItemHandler> inputCap;
    /** Bottom face: extract from output slots only. */
    protected final LazyOptional<IItemHandler> outputCap;

    protected final MachineEnergyStorage energy;
    protected final LazyOptional<IEnergyStorage> energyCap;

    /** Current progress in ticks (0..currentRecipeDuration). */
    protected int progress = 0;
    /** Duration of the currently running recipe. 0 when idle. */
    protected int currentRecipeDuration = 0;
    /** Energy-per-tick of the currently running recipe. */
    protected int currentRecipeEnergyPerTick = 0;

    protected AbstractMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                         int inputSlots, int outputSlots,
                                         int energyCapacity, int energyTransfer) {
        super(type, pos, state);
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.totalSlots = inputSlots + outputSlots;

        this.inventory = new ItemStackHandler(totalSlots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                // Output slots are not directly insertable from outside (only via crafting)
                return slot < inputSlots;
            }
        };

        IItemHandler inputView = new RangedWrapper(inventory, 0, inputSlots) {
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY; // input slots are insert-only externally
            }
        };
        IItemHandler outputView = new RangedWrapper(inventory, inputSlots, inputSlots + outputSlots) {
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return stack; // output slots are extract-only externally
            }
        };
        this.inputCap = LazyOptional.of(() -> inputView);
        this.outputCap = LazyOptional.of(() -> outputView);

        this.energy = new MachineEnergyStorage(energyCapacity, energyTransfer, e -> setChanged());
        this.energyCap = LazyOptional.of(() -> energy);
    }

    // ===== Subclass hooks =====

    /** Returns true if a recipe could start with the current inputs, and configures
     *  the progress/duration/energy-per-tick fields if so. */
    protected abstract boolean tryStartRecipe();

    /** Called when progress reaches the duration. Subclasses produce the output. */
    protected abstract void completeRecipe();

    /** Subclasses may override to short-circuit the tick (e.g. no input present). */
    protected boolean shouldRun() {
        return !inventory.getStackInSlot(0).isEmpty();
    }

    // ===== Tick =====

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractMachineBlockEntity be) {
        if (level.isClientSide()) return;
        be.tick();
    }

    protected void tick() {
        if (currentRecipeDuration == 0) {
            // idle — try to start a recipe
            if (shouldRun() && tryStartRecipe()) {
                progress = 0;
                setChanged();
            }
            return;
        }
        // running — consume energy & advance
        if (energy.getEnergyStored() < currentRecipeEnergyPerTick) {
            // not enough energy; pause until power returns
            setChanged();
            return;
        }
        energy.useEnergy(currentRecipeEnergyPerTick);
        progress++;
        if (progress >= currentRecipeDuration) {
            completeRecipe();
            progress = 0;
            currentRecipeDuration = 0;
            currentRecipeEnergyPerTick = 0;
        }
        setChanged();
    }

    /** Build a SimpleContainer view of the input slot(s) for recipe matching. */
    protected SimpleContainer inputContainerView() {
        SimpleContainer c = new SimpleContainer(inputSlots);
        for (int i = 0; i < inputSlots; i++) c.setItem(i, inventory.getStackInSlot(i));
        return c;
    }

    // ===== Capabilities =====

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            // Energy on any side except the front (player-facing). Front cap blocked.
            if (side == null || side != frontFace()) return energyCap.cast();
            return LazyOptional.empty();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.UP) return inputCap.cast();
            if (side == Direction.DOWN) return outputCap.cast();
            if (side == null) {
                // null side = generic; return the full handler (used by hoppers facing the block from front)
                return LazyOptional.of(() -> (IItemHandler) inventory).cast();
            }
            // remaining sides: no item I/O for the simple template
            return LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }

    /** Subclasses can override if the block has a FACING property. Defaults to NORTH. */
    protected Direction frontFace() { return Direction.NORTH; }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inputCap.invalidate();
        outputCap.invalidate();
        energyCap.invalidate();
    }

    // ===== Save/load =====

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("Progress", progress);
        tag.putInt("RecipeDuration", currentRecipeDuration);
        tag.putInt("RecipeEnergyPerTick", currentRecipeEnergyPerTick);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        energy.setEnergyDirectly(tag.getInt("Energy"));
        progress = tag.getInt("Progress");
        currentRecipeDuration = tag.getInt("RecipeDuration");
        currentRecipeEnergyPerTick = tag.getInt("RecipeEnergyPerTick");
    }

    /** Drop inventory contents on block break. Called from the Block.onRemove override. */
    public void dropContents() {
        if (level == null) return;
        NonNullList<ItemStack> list = NonNullList.withSize(totalSlots, ItemStack.EMPTY);
        for (int i = 0; i < totalSlots; i++) list.set(i, inventory.getStackInSlot(i));
        Container holder = new SimpleContainer(list.toArray(new ItemStack[0]));
        net.minecraft.world.Containers.dropContents(level, getBlockPos(), holder);
    }

    // ===== Accessors used by the Menu data slots =====

    public int getProgress() { return progress; }
    public int getMaxProgress() { return currentRecipeDuration; }
    public int getEnergyStored() { return energy.getEnergyStored(); }
    public int getEnergyCapacity() { return energy.getMaxEnergyStored(); }
    public ItemStackHandler getInventory() { return inventory; }
    public int getInputSlotCount() { return inputSlots; }
    public int getOutputSlotCount() { return outputSlots; }

    /** Allow the menu to validate the player can interact (within 8 blocks). */
    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

}
