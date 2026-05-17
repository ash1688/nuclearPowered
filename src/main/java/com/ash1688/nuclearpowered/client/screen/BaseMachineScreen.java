package com.ash1688.nuclearpowered.client.screen;

import com.ash1688.nuclearpowered.NuclearPowered;
import com.ash1688.nuclearpowered.machine.BaseMachineMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Shared screen for every machine in M3. Subclasses (or instances) supply the background
 * texture name; this class draws the energy bar (right edge), progress arrow (centre),
 * and tooltip on hover. Fluid tanks rendered by {@link FluidMachineScreen}.
 */
public class BaseMachineScreen<M extends BaseMachineMenu> extends AbstractContainerScreen<M> {
    public static final int W = 176;
    public static final int H = 166;
    protected final ResourceLocation bg;
    protected final int progressArrowX, progressArrowY;

    public BaseMachineScreen(M menu, Inventory inv, Component title,
                             String textureName, int progressArrowX, int progressArrowY) {
        super(menu, inv, title);
        this.bg = new ResourceLocation(NuclearPowered.MODID, "textures/gui/container/" + textureName + ".png");
        this.progressArrowX = progressArrowX;
        this.progressArrowY = progressArrowY;
        this.imageWidth = W;
        this.imageHeight = H;
        this.inventoryLabelY = H - 94;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        g.blit(bg, x, y, 0, 0, imageWidth, imageHeight);

        // Progress arrow (placeholder: filled rect since no texture art)
        int progress = menu.progress();
        int max = menu.maxProgress();
        if (max > 0 && progress > 0) {
            int w = (progress * 22) / max;
            g.fill(x + progressArrowX, y + progressArrowY, x + progressArrowX + w, y + progressArrowY + 14, 0xFFCCCCCC);
        }

        // Energy bar on the right (12 wide x 52 tall, at offset 152,14)
        drawVerticalBar(g, x + 152, y + 14, 12, 52, menu.energy(), menu.maxEnergy(), 0xFFE74C3C);
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (isInBounds(mouseX, mouseY, x + 152, y + 14, 12, 52)) {
            g.renderTooltip(font,
                    Component.literal(menu.energy() + " / " + menu.maxEnergy() + " FE"),
                    mouseX, mouseY);
        }
    }

    protected void drawVerticalBar(GuiGraphics g, int x, int y, int w, int h, int stored, int max, int colour) {
        if (max <= 0) {
            // outline only
            outline(g, x - 1, y - 1, w + 2, h + 2);
            return;
        }
        int fillH = (stored * h) / max;
        g.fill(x, y + (h - fillH), x + w, y + h, colour);
        outline(g, x - 1, y - 1, w + 2, h + 2);
    }

    protected void outline(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + 1, 0xFF555555);
        g.fill(x, y + h - 1, x + w, y + h, 0xFF555555);
        g.fill(x, y, x + 1, y + h, 0xFF555555);
        g.fill(x + w - 1, y, x + w, y + h, 0xFF555555);
    }

    protected boolean isInBounds(int mx, int my, int bx, int by, int bw, int bh) {
        return mx >= bx && mx < bx + bw && my >= by && my < by + bh;
    }
}
