package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.NPConfig;
import com.ash1688.nuclearpowered.init.NPBlockEntities;
import com.ash1688.nuclearpowered.init.NPFluids;
import com.ash1688.nuclearpowered.power.BootstrapRates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Coal Boiler (Bootstrap tier, ADR 0002): furnace fuel + water → steam. Not recipe-driven —
 * fuel value is Forge burn time. Reuses the machine base for slots, tanks, sidedness and
 * save data: {@code progress} is burn ticks left, {@code currentRecipeDuration} the current
 * fuel's total burn. Pushes steam into any adjacent fluid handler (normally a Steam Engine).
 */
public final class CoalBoilerBlockEntity extends AbstractMachineBlockEntity implements MenuProvider {
    public static final int TANK_CAPACITY = 8_000;

    public CoalBoilerBlockEntity(BlockPos pos, BlockState state) {
        super(NPBlockEntities.COAL_BOILER.get(), pos, state, 1, 0, 0, 0, TANK_CAPACITY, TANK_CAPACITY);
    }

    @Override protected boolean isValidInputItem(int slot, ItemStack stack) { return ForgeHooks.getBurnTime(stack, null) > 0; }
    @Override protected boolean isValidInputFluid(FluidStack stack) { return stack.getFluid().is(FluidTags.WATER); }

    @Override protected Direction frontFace() {
        BlockState s = getBlockState();
        return s.hasProperty(HorizontalDirectionalBlock.FACING) ? s.getValue(HorizontalDirectionalBlock.FACING) : Direction.NORTH;
    }

    // Not recipe-driven; tick() is replaced wholesale.
    @Override protected boolean tryStartRecipe() { return false; }
    @Override protected void completeRecipe() {}

    @Override
    protected void tick() {
        int waterPerTick = NPConfig.COMMON.boilerWaterPerTick.get();
        int steamPerTick = NPConfig.COMMON.boilerSteamPerTick.get();
        int water = inputFluidTank.getFluidAmount();
        int steamSpace = outputFluidTank.getSpace();

        if (progress <= 0 && BootstrapRates.canBoil(water, steamSpace, waterPerTick, steamPerTick)) lightFuel();

        BootstrapRates.BoilerStep step = BootstrapRates.boilerStep(progress, water, steamSpace, waterPerTick, steamPerTick);
        if (step.burnAdvances()) {
            inputFluidTank.drain(step.waterUsed(), IFluidHandler.FluidAction.EXECUTE);
            outputFluidTank.fill(new FluidStack(NPFluids.STEAM.get(), step.steamMade()), IFluidHandler.FluidAction.EXECUTE);
            if (--progress <= 0) currentRecipeDuration = 0;
            setChanged();
        }
        pushSteam();
    }

    private void lightFuel() {
        ItemStack fuel = inventory.getStackInSlot(0);
        int burn = ForgeHooks.getBurnTime(fuel, null);
        if (burn <= 0) return;
        ItemStack remainder = fuel.getCraftingRemainingItem();  // e.g. lava bucket → bucket
        inventory.extractItem(0, 1, false);
        if (!remainder.isEmpty() && inventory.getStackInSlot(0).isEmpty()) inventory.setStackInSlot(0, remainder);
        progress = burn;
        currentRecipeDuration = burn;
        setChanged();
    }

    private void pushSteam() {
        if (level == null || outputFluidTank.isEmpty()) return;
        for (Direction dir : Direction.values()) {
            if (outputFluidTank.isEmpty()) return;
            BlockEntity neighbour = level.getBlockEntity(worldPosition.relative(dir));
            if (neighbour == null) continue;
            neighbour.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(target -> {
                int accepted = target.fill(outputFluidTank.getFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                if (accepted > 0) {
                    outputFluidTank.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
                    setChanged();
                }
            });
        }
    }

    @Override public @NotNull Component getDisplayName() { return Component.translatable("block.nuclearpowered.coal_boiler"); }
    @Override public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player p) { return new CoalBoilerMenu(id, inv, this); }
}
