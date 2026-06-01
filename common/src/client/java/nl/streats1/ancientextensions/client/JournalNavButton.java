package nl.streats1.ancientextensions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Leather-styled page navigation for the Regional Survey field log.
 */
public class JournalNavButton extends Button {

    static final int SPRITE_W = JournalWidgetSprites.NAV_W;
    static final int SPRITE_H = JournalWidgetSprites.NAV_H;

    private final boolean next;

    public JournalNavButton(int x, int y, boolean next, OnPress onPress) {
        super(x, y, SPRITE_W, SPRITE_H, Component.empty(), onPress, DEFAULT_NARRATION);
        this.next = next;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            return;
        }
        int u = next ? JournalWidgetSprites.NEXT_U : JournalWidgetSprites.PREV_U;
        int v = JournalWidgetSprites.NAV_V + (this.active && this.isHoveredOrFocused() ? SPRITE_H : 0);
        graphics.blit(
                JournalWidgetSprites.TEXTURE,
                getX(),
                getY(),
                u,
                v,
                getWidth(),
                getHeight(),
                JournalWidgetSprites.TEX_W,
                JournalWidgetSprites.TEX_H
        );
    }
}
