package com.ash1688.nuclearpowered.client.screen;

import com.ash1688.nuclearpowered.machine.SteamEngineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/** Steam tank on the left, FE buffer on the right, and the current FE/t against the engine's cap. */
public class SteamEngineScreen extends FluidMachineScreen<SteamEngineMenu> {
    public SteamEngineScreen(SteamEngineMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, "steam_engine", 79, 35, true, 52, 14, false, 0, 0);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics g, int mouseX, int mouseY) {
        super.renderLabels(g, mouseX, mouseY);
        // menu.progress() = last tick's FE/t, menu.maxProgress() = configured cap (see SteamEngineBlockEntity).
        g.drawString(font, Component.translatable("gui.nuclearpowered.fe_per_tick", menu.progress(), menu.maxProgress()),
                72, 56, 0x404040, false);
    }
}
