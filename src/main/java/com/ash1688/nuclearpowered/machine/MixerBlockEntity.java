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

public final class MixerBlockEntity extends AbstractMachineBlockEntity implements MenuProvider {
    public MixerBlockEntity(BlockPos pos, BlockState state) {
        super(NPBlockEntities.MIXER.get(), pos, state, 2, 1, DEFAULT_ENERGY_CAPACITY, DEFAULT_ENERGY_TRANSFER);
    }
    @Override protected Direction frontFace() {
        BlockState s = getBlockState();
        return s.hasProperty(HorizontalDirectionalBlock.FACING) ? s.getValue(HorizontalDirectionalBlock.FACING) : Direction.NORTH;
    }
    @Override protected boolean tryStartRecipe() { return MachineRecipeLogic.tryStartGeneric(this, NPRecipes.MIXER_TYPE.get()); }
    @Override protected void completeRecipe() { MachineRecipeLogic.completeGeneric(this, NPRecipes.MIXER_TYPE.get()); }
    @Override public @NotNull Component getDisplayName() { return Component.translatable("block.nuclearpowered.mixer"); }
    @Override public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player p) { return new MixerMenu(id, inv, this); }
}
