package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Leather-styled page navigation for the Regional Survey field log.
 */
public class JournalNavButton extends Button {

    static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientExtensionsConstants.MOD_ID,
            "textures/gui/regional_survey_journal.png"
    );
    static final int TEX_SIZE = 256;
    static final int SPRITE_W = 22;
    static final int SPRITE_H = 16;

    private static final int PREV_U = 112;
    private static final int NEXT_U = 134;
    private static final int SPRITE_V = 220;

    private static final int LABEL_COLOR = 0xFF2A1810;

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
        int u = next ? NEXT_U : PREV_U;
        int v = SPRITE_V + (this.active && this.isHoveredOrFocused() ? SPRITE_H : 0);
        graphics.blit(
                TEXTURE,
                getX(),
                getY(),
                u,
                v,
                getWidth(),
                getHeight(),
                TEX_SIZE,
                TEX_SIZE
        );
    }

    @Override
    public void renderString(GuiGraphics graphics, Font font, int color) {
        String glyph = next ? "›" : "‹";
        Component label = Component.literal(glyph).withColor(LABEL_COLOR);
        GuiTextRender.drawCenteredStyled(
                font,
                graphics,
                label,
                getX() + getWidth() / 2,
                getY() + (getHeight() - 8) / 2,
                false
        );
    }
}
