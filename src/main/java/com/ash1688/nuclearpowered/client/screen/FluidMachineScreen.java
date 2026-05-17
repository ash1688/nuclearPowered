package com.ash1688.nuclearpowered.client.screen;

import com.ash1688.nuclearpowered.machine.BaseMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Extends BaseMachineScreen with fluid tank renderings. Subclasses configure which tanks
 * to show and where via {@link #showInputFluid} / {@link #showOutputFluid}.
 */
public class FluidMachineScreen<M extends BaseMachineMenu> extends BaseMachineScreen<M> {
    protected final boolean showInputFluid;
    protected final int inFluidX, inFluidY;
    protected final boolean showOutputFluid;
    protected final int outFluidX, outFluidY;

    public FluidMachineScreen(M menu, Inventory inv, Component title,
                              String textureName, int progressArrowX, int progressArrowY,
                              boolean showInputFluid, int inFluidX, int inFluidY,
                              boolean showOutputFluid, int outFluidX, int outFluidY) {
        super(menu, inv, title, textureName, progressArrowX, progressArrowY);
        this.showInputFluid = showInputFluid;
        this.inFluidX = inFluidX;
        this.inFluidY = inFluidY;
        this.showOutputFluid = showOutputFluid;
        this.outFluidX = outFluidX;
        this.outFluidY = outFluidY;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        super.renderBg(g, partialTick, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (showInputFluid) {
            drawVerticalBar(g, x + inFluidX, y + inFluidY, 12, 52,
                    menu.inputFluidAmount(), menu.inputFluidCapacity(), 0xFF3498DB);
        }
        if (showOutputFluid) {
            drawVerticalBar(g, x + outFluidX, y + outFluidY, 12, 52,
                    menu.outputFluidAmount(), menu.outputFluidCapacity(), 0xFF9B59B6);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (showInputFluid && isInBounds(mouseX, mouseY, x + inFluidX, y + inFluidY, 12, 52)) {
            g.renderTooltip(font, Component.literal(
                    menu.inputFluidAmount() + " / " + menu.inputFluidCapacity() + " mB (input)"),
                    mouseX, mouseY);
        }
        if (showOutputFluid && isInBounds(mouseX, mouseY, x + outFluidX, y + outFluidY, 12, 52)) {
            g.renderTooltip(font, Component.literal(
                    menu.outputFluidAmount() + " / " + menu.outputFluidCapacity() + " mB (output)"),
                    mouseX, mouseY);
        }
    }
}
