package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlockEntities;
import com.ash1688.nuclearpowered.init.NPRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Gaseous Diffusion Chamber: 1 item in (UF4 powder) → 1 fluid out (UF6). */
public final class GaseousDiffusionBlockEntity extends AbstractMachineBlockEntity implements MenuProvider {
    public GaseousDiffusionBlockEntity(BlockPos pos, BlockState state) {
        super(NPBlockEntities.GASEOUS_DIFFUSION.get(), pos, state,
                1, 0, DEFAULT_ENERGY_CAPACITY, DEFAULT_ENERGY_TRANSFER,
                0, DEFAULT_FLUID_CAPACITY);
    }
    @Override protected Direction frontFace() {
        BlockState s = getBlockState();
        return s.hasProperty(HorizontalDirectionalBlock.FACING) ? s.getValue(HorizontalDirectionalBlock.FACING) : Direction.NORTH;
    }
    @Override protected boolean tryStartRecipe() { return MachineRecipeLogic.tryStartGeneric(this, NPRecipes.GASEOUS_DIFFUSION_TYPE.get()); }
    @Override protected void completeRecipe() { MachineRecipeLogic.completeGeneric(this, NPRecipes.GASEOUS_DIFFUSION_TYPE.get()); }
    @Override public @NotNull Component getDisplayName() { return Component.translatable("block.nuclearpowered.gaseous_diffusion"); }
    @Override public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player p) { return new GaseousDiffusionMenu(id, inv, this); }
}
