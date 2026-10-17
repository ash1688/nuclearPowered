package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.recipe.MachineRecipe;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;

/**
 * Shared recipe-driving logic used by every machine block entity. Keeps the per-machine
 * BlockEntity classes trivial — they just route to here with their RecipeType.
 *
 * Handles all combinations of: N item inputs, optional fluid input, 1-2 item outputs,
 * optional fluid output, energy & duration. The block entity supplies its inventory and
 * tanks; this class checks recipe match, capacity, and applies consumption + production.
 */
public final class MachineRecipeLogic {

    private MachineRecipeLogic() {}

    /** 1+-in / 1-out item-only machines (Crusher, Cutter, Macerator, Mixer, FuelRodAssembly). */
    public static boolean tryStartItemOnlyRecipe(AbstractMachineBlockEntity be, RecipeType<MachineRecipe> type) {
        return tryStartGeneric(be, type);
    }

    /** Generic recipe-start. Handles fluid I/O too. */
    public static boolean tryStartGeneric(AbstractMachineBlockEntity be, RecipeType<MachineRecipe> type) {
        if (be.getLevel() == null) return false;
        Optional<MachineRecipe> match = lookupRecipe(be, type);
        if (match.isEmpty()) return false;
        MachineRecipe r = match.get();

        // Verify input fluid availability
        FluidStack inFluid = r.inputFluid();
        if (!inFluid.isEmpty()) {
            FluidTank tank = be.getInputFluidTank();
            if (tank == null) return false;
            if (!tank.getFluid().isFluidEqual(inFluid) || tank.getFluidAmount() < inFluid.getAmount()) return false;
        }

        // Verify main output capacity (item)
        ItemStack mainResult = r.mainResult();
        if (!mainResult.isEmpty()) {
            int outIdx = be.getInputSlotCount();  // primary output is the first output slot
            if (!canFit(be.getInventory(), outIdx, mainResult)) return false;
        }

        // Verify secondary output capacity if used
        ItemStack secondary = r.secondaryResult();
        if (!secondary.isEmpty() && be.getOutputSlotCount() >= 2) {
            int outIdx2 = be.getInputSlotCount() + 1;
            if (!canFit(be.getInventory(), outIdx2, secondary)) return false;
        }

        // Verify output fluid capacity
        FluidStack outFluid = r.outputFluid();
        if (!outFluid.isEmpty()) {
            FluidTank tank = be.getOutputFluidTank();
            if (tank == null) return false;
            int filled = tank.fill(outFluid.copy(), IFluidHandler.FluidAction.SIMULATE);
            if (filled < outFluid.getAmount()) return false;
        }

        // Verify enough energy for at least one tick
        int perTick = Math.max(1, r.energy() / Math.max(1, r.duration()));
        if (be.getEnergyStored() < perTick) return false;

        // Commit start
        be.currentRecipeDuration = r.duration();
        be.currentRecipeEnergyPerTick = perTick;
        return true;
    }

    /** 1+-in / 1-out item-only machines complete recipe. */
    public static void completeItemOnlyRecipe(AbstractMachineBlockEntity be, RecipeType<MachineRecipe> type) {
        completeGeneric(be, type);
    }

    /** Generic recipe-complete. */
    public static void completeGeneric(AbstractMachineBlockEntity be, RecipeType<MachineRecipe> type) {
        if (be.getLevel() == null) return;
        Optional<MachineRecipe> match = lookupRecipe(be, type);
        if (match.isEmpty()) return;
        MachineRecipe r = match.get();

        ItemStackHandler inv = be.getInventory();

        // Consume each item input by 1
        for (int i = 0; i < r.ingredients().size(); i++) {
            inv.extractItem(i, 1, false);
        }
        // Consume input fluid
        if (!r.inputFluid().isEmpty() && be.getInputFluidTank() != null) {
            be.getInputFluidTank().drain(r.inputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
        }

        // Produce main output (item)
        if (!r.mainResult().isEmpty()) {
            insertOrMerge(inv, be.getInputSlotCount(), r.mainResult());
        }
        // Produce secondary output (item)
        if (!r.secondaryResult().isEmpty() && be.getOutputSlotCount() >= 2) {
            int chance = Math.max(0, Math.min(100, r.secondaryChance()));
            if (chance >= 100 || (chance > 0 && be.getLevel().random.nextInt(100) < chance)) {
                insertOrMerge(inv, be.getInputSlotCount() + 1, r.secondaryResult());
            }
        }
        // Produce output fluid
        if (!r.outputFluid().isEmpty() && be.getOutputFluidTank() != null) {
            be.getOutputFluidTank().fill(r.outputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private static boolean canFit(ItemStackHandler inv, int slot, ItemStack toAdd) {
        ItemStack existing = inv.getStackInSlot(slot);
        if (existing.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(existing, toAdd)) return false;
        return existing.getCount() + toAdd.getCount() <= existing.getMaxStackSize();
    }

    /**
     * Merge a result into an output slot via setStackInSlot (not insertItem — the handler's
     * isItemValid rejects output slots, which would void every result). Stack size is capped
     * and onContentsChanged still fires. We pre-verified canFit() at recipe start, so overflow
     * here means the slot changed mid-recipe — log it instead of silently losing.
     */
    private static void insertOrMerge(ItemStackHandler inv, int slot, ItemStack toAdd) {
        ItemStack existing = inv.getStackInSlot(slot);
        int moved;
        if (existing.isEmpty()) {
            moved = Math.min(toAdd.getCount(), toAdd.getMaxStackSize());
            inv.setStackInSlot(slot, toAdd.copyWithCount(moved));
        } else if (ItemStack.isSameItemSameTags(existing, toAdd)) {
            moved = Math.min(toAdd.getCount(), existing.getMaxStackSize() - existing.getCount());
            inv.setStackInSlot(slot, existing.copyWithCount(existing.getCount() + moved));
        } else {
            moved = 0;
        }
        if (moved < toAdd.getCount()) {
            NuclearPowered.LOGGER.warn("Machine output overflow at slot {} ({} x{} dropped from result); "
                    + "slot contents changed during recipe?", slot, toAdd.getItem(), toAdd.getCount() - moved);
        }
    }

    /** True if any recipe of this type consumes the given fluid. Used to gate external tank fills. */
    public static boolean isRecipeInputFluid(AbstractMachineBlockEntity be, RecipeType<MachineRecipe> type, FluidStack stack) {
        if (be.getLevel() == null) return true;  // during load; tank contents come from NBT, not fill()
        for (MachineRecipe r : be.getLevel().getRecipeManager().getAllRecipesFor(type)) {
            if (!r.inputFluid().isEmpty() && r.inputFluid().isFluidEqual(stack)) return true;
        }
        return false;
    }

    /**
     * Look up a recipe, using the BE's cached match when its inputs still satisfy it.
     * Cache is invalidated on input slot / input-fluid changes via AbstractMachineBlockEntity.
     */
    private static Optional<MachineRecipe> lookupRecipe(AbstractMachineBlockEntity be, RecipeType<MachineRecipe> type) {
        SimpleContainer view = be.inputContainerView();
        if (be.cachedRecipe.isPresent() && be.cachedRecipe.get().matches(view, be.getLevel())) {
            return be.cachedRecipe;
        }
        Optional<MachineRecipe> fresh = be.getLevel().getRecipeManager().getRecipeFor(type, view, be.getLevel());
        be.cachedRecipe = fresh;
        return fresh;
    }
}
