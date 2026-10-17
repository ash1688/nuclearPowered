package com.ash1688.nuclearpowered.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Hot Spent Fuel Rod — what every reactor ejects (see CONTEXT.md). Hurts and ignites the
 * holder while it sits in the selected hotbar slot; harmless anywhere else in the inventory.
 * It never cools on its own: only a Cooling Pond / Spent Fuel Pool turns it into a Spent Fuel Rod.
 */
public class HotSpentFuelRodItem extends Item {
    public static final int DAMAGE_INTERVAL_TICKS = 20;
    public static final float DAMAGE_PER_HIT = 1.0F;
    public static final int FIRE_SECONDS = 2;

    public HotSpentFuelRodItem(Properties props) { super(props); }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (level.isClientSide() || !selected || !(entity instanceof LivingEntity holder)) return;
        if (holder.tickCount % DAMAGE_INTERVAL_TICKS != 0) return;
        holder.hurt(level.damageSources().inFire(), DAMAGE_PER_HIT);
        holder.setSecondsOnFire(FIRE_SECONDS);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.nuclearpowered.hot_spent_fuel_rod.tooltip").withStyle(ChatFormatting.RED));
    }
}
