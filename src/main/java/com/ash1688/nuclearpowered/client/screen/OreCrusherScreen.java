package com.ash1688.nuclearpowered.client.screen;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.machine.OreCrusherMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Ore Crusher GUI. Uses a custom background texture at
 * assets/nuclearpowered/textures/gui/container/ore_crusher.png (176x166 standard).
 *
 * Until the real art is delivered, this falls back gracefully — Minecraft will render
 * the missing-texture purple/black checkers, but the GUI is still usable.
 */
public final class OreCrusherScreen extends AbstractContainerScreen<OreCrusherMenu> {
    private static final ResourceLocation BG = new ResourceLocation(NuclearPowered.MODID,
            "textures/gui/container/ore_crusher.png");

    public OreCrusherScreen(OreCrusherMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        g.blit(BG, x, y, 0, 0, imageWidth, imageHeight);

        // Progress arrow: 22x16 from texture at (176,14) drawn at (79, 34) relative.
        // We draw a simple filled rectangle as a placeholder while no texture exists.
        int progress = menu.progress();
        int max = menu.maxProgress();
        if (max > 0 && progress > 0) {
            int w = (progress * 22) / max;
            g.fill(x + 79, y + 35, x + 79 + w, y + 35 + 14, 0xFFCCCCCC);
        }

        // Energy bar: vertical, on right edge of the GUI background.
        int storedE = menu.energy();
        int maxE = menu.maxEnergy();
        int fullH = 52;
        if (maxE > 0) {
            int h = (storedE * fullH) / maxE;
            int barX = x + 152;
            int barTop = y + 14;
            g.fill(barX, barTop + (fullH - h), barX + 12, barTop + fullH, 0xFFE74C3C);
        }
        // Energy bar outline
        g.fill(x + 151, y + 13, x + 165, y + 14, 0xFF555555);
        g.fill(x + 151, y + 14 + 52, x + 165, y + 14 + 53, 0xFF555555);
        g.fill(x + 151, y + 13, x + 152, y + 14 + 53, 0xFF555555);
        g.fill(x + 164, y + 13, x + 165, y + 14 + 53, 0xFF555555);
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);

        // Energy tooltip on hover
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (mouseX >= x + 151 && mouseX <= x + 165 && mouseY >= y + 13 && mouseY <= y + 13 + 53) {
            g.renderTooltip(font,
                    Component.literal(menu.energy() + " / " + menu.maxEnergy() + " FE"),
                    mouseX, mouseY);
        }
    }
}
