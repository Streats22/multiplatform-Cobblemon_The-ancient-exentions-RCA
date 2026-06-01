package nl.streats1.ancientextensions.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Gold-stamped claim control for the survey journal footer strip.
 */
public class JournalClaimButton extends Button {

    static final int SPRITE_H = JournalWidgetSprites.CLAIM_H;

    private static final int LABEL_COLOR = 0xFF2A1810;

    public JournalClaimButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            return;
        }
        int v = this.isHoveredOrFocused() ? SPRITE_H : 0;
        graphics.blit(
                JournalWidgetSprites.TEXTURE,
                getX(),
                getY(),
                JournalWidgetSprites.CLAIM_U,
                JournalWidgetSprites.CLAIM_V + v,
                getWidth(),
                getHeight(),
                JournalWidgetSprites.TEX_W,
                JournalWidgetSprites.TEX_H
        );
        renderString(graphics, Minecraft.getInstance().font, LABEL_COLOR);
    }

    @Override
    public void renderString(GuiGraphics graphics, Font font, int color) {
        Component label = getMessage().copy().withColor(LABEL_COLOR);
        GuiTextRender.drawCenteredStyledSoft(
                font,
                graphics,
                label,
                getX() + getWidth() / 2,
                getY() + (getHeight() - 8) / 2
        );
    }
}
