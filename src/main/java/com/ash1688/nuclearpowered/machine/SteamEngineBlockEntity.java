package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.NPConfig;
import com.ash1688.nuclearpowered.init.NPBlockEntities;
import com.ash1688.nuclearpowered.power.BootstrapRates;
import com.ash1688.nuclearpowered.power.FEOutputBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Steam Engine (Bootstrap tier, ADR 0002): steam → FE at a low capped rate, pushed to every
 * neighbour. Accepts any fluid in {@code forge:steam}. Steam input is open on every face (no
 * output tank to share faces with). For the GUI, {@code progress}/{@code maxProgress} carry the
 * last tick's FE/t and the configured cap.
 */
public final class SteamEngineBlockEntity extends AbstractMachineBlockEntity implements MenuProvider {
    public static final TagKey<Fluid> STEAM_TAG = FluidTags.create(new ResourceLocation("forge", "steam"));
    public static final int TANK_CAPACITY = 8_000;
    public static final int BUFFER_CAPACITY = 10_000;

    private final FEOutputBuffer output = new FEOutputBuffer(BUFFER_CAPACITY, Integer.MAX_VALUE);
    private final LazyOptional<IEnergyStorage> outputCap = LazyOptional.of(() -> output);
    private int lastFePerTick = 0;

    public SteamEngineBlockEntity(BlockPos pos, BlockState state) {
        super(NPBlockEntities.STEAM_ENGINE.get(), pos, state, 0, 0, 0, 0, TANK_CAPACITY, 0);
    }

    @Override protected boolean isValidInputFluid(FluidStack stack) { return stack.getFluid().is(STEAM_TAG); }

    @Override protected Direction frontFace() {
        BlockState s = getBlockState();
        return s.hasProperty(HorizontalDirectionalBlock.FACING) ? s.getValue(HorizontalDirectionalBlock.FACING) : Direction.NORTH;
    }

    // Not recipe-driven; tick() is replaced wholesale.
    @Override protected boolean tryStartRecipe() { return false; }
    @Override protected void completeRecipe() {}

    @Override
    protected void tick() {
        int cap = NPConfig.COMMON.steamEngineMaxFePerTick.get();
        BootstrapRates.EngineStep step = BootstrapRates.engineStep(
                inputFluidTank.getFluidAmount(), output.freeSpace(), cap, NPConfig.COMMON.steamEngineFePerMb.get());
        if (step.steamUsed() > 0) {
            inputFluidTank.drain(step.steamUsed(), IFluidHandler.FluidAction.EXECUTE);
            output.generate(step.feMade());
        }
        lastFePerTick = step.feMade();
        if (level != null) output.pushToNeighbours(level, worldPosition, cap);
        if (output.isDirtyAndClear() || step.steamUsed() > 0) setChanged();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return outputCap.cast();
        if (cap == ForgeCapabilities.FLUID_HANDLER) return inputFluidCap.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        outputCap.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("OutputEnergy", output.getEnergyStored());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        output.setEnergyDirectly(tag.getInt("OutputEnergy"));
    }

    @Override public int getEnergyStored() { return output.getEnergyStored(); }
    @Override public int getEnergyCapacity() { return output.getMaxEnergyStored(); }
    @Override public int getProgress() { return lastFePerTick; }
    @Override public int getMaxProgress() { return NPConfig.COMMON.steamEngineMaxFePerTick.get(); }

    @Override public @NotNull Component getDisplayName() { return Component.translatable("block.nuclearpowered.steam_engine"); }
    @Override public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player p) { return new SteamEngineMenu(id, inv, this); }
}
