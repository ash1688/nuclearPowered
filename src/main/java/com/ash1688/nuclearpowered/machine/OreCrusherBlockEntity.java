package com.ash1688.nuclearpowered.machine;

import com.ash1688.nuclearpowered.init.NPBlockEntities;
import com.ash1688.nuclearpowered.init.NPRecipes;
import com.ash1688.nuclearpowered.recipe.OreCrusherRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class OreCrusherBlockEntity extends AbstractMachineBlockEntity implements MenuProvider {
    public OreCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(NPBlockEntities.ORE_CRUSHER.get(), pos, state,
                1, 1,
                DEFAULT_ENERGY_CAPACITY, DEFAULT_ENERGY_TRANSFER);
    }

    @Override
    protected Direction frontFace() {
        BlockState s = getBlockState();
        return s.hasProperty(HorizontalDirectionalBlock.FACING)
                ? s.getValue(HorizontalDirectionalBlock.FACING)
                : Direction.NORTH;
    }

    @Override
    protected boolean tryStartRecipe() {
        if (level == null) return false;
        Optional<OreCrusherRecipe> match = level.getRecipeManager()
                .getRecipeFor(NPRecipes.ORE_CRUSHER_TYPE.get(), inputContainerView(), level);
        if (match.isEmpty()) return false;
        OreCrusherRecipe r = match.get();
        // Verify result will fit in output slot
        ItemStack out = inventory.getStackInSlot(inputSlots);
        ItemStack toProduce = r.getResultItem(level.registryAccess());
        if (!out.isEmpty()) {
            if (!ItemStack.isSameItemSameTags(out, toProduce)) return false;
            if (out.getCount() + toProduce.getCount() > out.getMaxStackSize()) return false;
        }
        // Verify enough energy budget exists for at least the first tick
        int perTick = Math.max(1, r.energy() / r.duration());
        if (energy.getEnergyStored() < perTick) return false;
        this.currentRecipeDuration = r.duration();
        this.currentRecipeEnergyPerTick = perTick;
        return true;
    }

    @Override
    protected void completeRecipe() {
        if (level == null) return;
        Optional<OreCrusherRecipe> match = level.getRecipeManager()
                .getRecipeFor(NPRecipes.ORE_CRUSHER_TYPE.get(), inputContainerView(), level);
        if (match.isEmpty()) return;
        OreCrusherRecipe r = match.get();
        ItemStack result = r.getResultItem(level.registryAccess());
        // Consume one from input slot
        inventory.extractItem(0, 1, false);
        // Insert into output slot
        ItemStack out = inventory.getStackInSlot(inputSlots);
        if (out.isEmpty()) {
            inventory.setStackInSlot(inputSlots, result);
        } else {
            out.grow(result.getCount());
            inventory.setStackInSlot(inputSlots, out);
        }
    }

    // ===== MenuProvider =====

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.nuclearpowered.ore_crusher");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInv, @NotNull Player player) {
        return new OreCrusherMenu(id, playerInv, this);
    }
}
