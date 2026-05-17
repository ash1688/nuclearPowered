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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for single-block machines.
 *
 * Item I/O sidedness: TOP=input, BOTTOM=output, FRONT=blocked (player-facing), other=blocked.
 * Energy I/O: all faces except FRONT accept FE (insert-only externally).
 * Fluid I/O (when enabled): input tank exposed on TOP, output tank exposed on BOTTOM,
 * BACK exposes both for hybrid I/O.
 */
public abstract class AbstractMachineBlockEntity extends BlockEntity {
    public static final int DEFAULT_ENERGY_CAPACITY = 10_000;
    public static final int DEFAULT_ENERGY_TRANSFER = 200;
    public static final int DEFAULT_FLUID_CAPACITY = 4_000;

    protected final int inputSlots;
    protected final int outputSlots;
    protected final int totalSlots;

    protected final ItemStackHandler inventory;
    protected final LazyOptional<IItemHandler> inputCap;
    protected final LazyOptional<IItemHandler> outputCap;

    protected final MachineEnergyStorage energy;
    protected final LazyOptional<IEnergyStorage> energyCap;

    // Optional fluid tanks; if capacity is 0 the cap is empty.
    protected final FluidTank inputFluidTank;
    protected final FluidTank outputFluidTank;
    protected final LazyOptional<IFluidHandler> inputFluidCap;
    protected final LazyOptional<IFluidHandler> outputFluidCap;

    protected int progress = 0;
    protected int currentRecipeDuration = 0;
    protected int currentRecipeEnergyPerTick = 0;

    protected AbstractMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                         int inputSlots, int outputSlots,
                                         int energyCapacity, int energyTransfer,
                                         int inputFluidCapacity, int outputFluidCapacity) {
        super(type, pos, state);
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.totalSlots = inputSlots + outputSlots;

        this.inventory = new ItemStackHandler(totalSlots) {
            @Override protected void onContentsChanged(int slot) { setChanged(); }
            @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot < AbstractMachineBlockEntity.this.inputSlots;
            }
        };
        IItemHandler inputView = new RangedWrapper(inventory, 0, inputSlots) {
            @Override public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
        };
        IItemHandler outputView = new RangedWrapper(inventory, inputSlots, totalSlots) {
            @Override public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }
        };
        this.inputCap = LazyOptional.of(() -> inputView);
        this.outputCap = LazyOptional.of(() -> outputView);

        this.energy = new MachineEnergyStorage(energyCapacity, energyTransfer, e -> setChanged());
        this.energyCap = LazyOptional.of(() -> energy);

        this.inputFluidTank = inputFluidCapacity > 0 ? new FluidTank(inputFluidCapacity) {
            @Override protected void onContentsChanged() { setChanged(); }
        } : null;
        this.outputFluidTank = outputFluidCapacity > 0 ? new FluidTank(outputFluidCapacity) {
            @Override protected void onContentsChanged() { setChanged(); }
            @Override public boolean isFluidValid(FluidStack stack) { return true; }
        } : null;
        this.inputFluidCap = inputFluidTank != null ? LazyOptional.of(() -> (IFluidHandler) inputFluidTank) : LazyOptional.empty();
        this.outputFluidCap = outputFluidTank != null ? LazyOptional.of(() -> (IFluidHandler) outputFluidTank) : LazyOptional.empty();
    }

    /** Convenience for item-only machines (no fluids). */
    protected AbstractMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                         int inputSlots, int outputSlots,
                                         int energyCapacity, int energyTransfer) {
        this(type, pos, state, inputSlots, outputSlots, energyCapacity, energyTransfer, 0, 0);
    }

    protected abstract boolean tryStartRecipe();
    protected abstract void completeRecipe();

    protected boolean shouldRun() {
        for (int i = 0; i < inputSlots; i++) if (!inventory.getStackInSlot(i).isEmpty()) return true;
        return inputFluidTank != null && !inputFluidTank.isEmpty();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractMachineBlockEntity be) {
        if (level.isClientSide()) return;
        be.tick();
    }

    protected void tick() {
        if (currentRecipeDuration == 0) {
            if (shouldRun() && tryStartRecipe()) {
                progress = 0;
                setChanged();
            }
            return;
        }
        if (energy.getEnergyStored() < currentRecipeEnergyPerTick) {
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

    protected SimpleContainer inputContainerView() {
        SimpleContainer c = new SimpleContainer(inputSlots);
        for (int i = 0; i < inputSlots; i++) c.setItem(i, inventory.getStackInSlot(i));
        return c;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            if (side == null || side != frontFace()) return energyCap.cast();
            return LazyOptional.empty();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.UP) return inputCap.cast();
            if (side == Direction.DOWN) return outputCap.cast();
            if (side == null) return LazyOptional.of(() -> (IItemHandler) inventory).cast();
            return LazyOptional.empty();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            // Fluid sidedness: input tank on TOP, output tank on BOTTOM, both on BACK.
            Direction back = frontFace().getOpposite();
            if (side == Direction.UP && inputFluidTank != null) return inputFluidCap.cast();
            if (side == Direction.DOWN && outputFluidTank != null) return outputFluidCap.cast();
            if (side == back) {
                if (outputFluidTank != null) return outputFluidCap.cast();
                if (inputFluidTank != null) return inputFluidCap.cast();
            }
            if (side == null) {
                if (outputFluidTank != null) return outputFluidCap.cast();
                if (inputFluidTank != null) return inputFluidCap.cast();
            }
            return LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }

    protected Direction frontFace() { return Direction.NORTH; }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inputCap.invalidate();
        outputCap.invalidate();
        energyCap.invalidate();
        inputFluidCap.invalidate();
        outputFluidCap.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("Progress", progress);
        tag.putInt("RecipeDuration", currentRecipeDuration);
        tag.putInt("RecipeEnergyPerTick", currentRecipeEnergyPerTick);
        if (inputFluidTank != null) tag.put("InputFluid", inputFluidTank.writeToNBT(new CompoundTag()));
        if (outputFluidTank != null) tag.put("OutputFluid", outputFluidTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        energy.setEnergyDirectly(tag.getInt("Energy"));
        progress = tag.getInt("Progress");
        currentRecipeDuration = tag.getInt("RecipeDuration");
        currentRecipeEnergyPerTick = tag.getInt("RecipeEnergyPerTick");
        if (inputFluidTank != null) inputFluidTank.readFromNBT(tag.getCompound("InputFluid"));
        if (outputFluidTank != null) outputFluidTank.readFromNBT(tag.getCompound("OutputFluid"));
    }

    public void dropContents() {
        if (level == null) return;
        NonNullList<ItemStack> list = NonNullList.withSize(totalSlots, ItemStack.EMPTY);
        for (int i = 0; i < totalSlots; i++) list.set(i, inventory.getStackInSlot(i));
        Container holder = new SimpleContainer(list.toArray(new ItemStack[0]));
        net.minecraft.world.Containers.dropContents(level, getBlockPos(), holder);
    }

    public int getProgress() { return progress; }
    public int getMaxProgress() { return currentRecipeDuration; }
    public int getEnergyStored() { return energy.getEnergyStored(); }
    public int getEnergyCapacity() { return energy.getMaxEnergyStored(); }
    public ItemStackHandler getInventory() { return inventory; }
    public int getInputSlotCount() { return inputSlots; }
    public int getOutputSlotCount() { return outputSlots; }
    public @Nullable FluidTank getInputFluidTank() { return inputFluidTank; }
    public @Nullable FluidTank getOutputFluidTank() { return outputFluidTank; }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }
}
