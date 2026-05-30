package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Gold-stamped claim control for the survey journal footer strip.
 */
public class JournalClaimButton extends Button {

    static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientExtensionsConstants.MOD_ID,
            "textures/gui/regional_survey_journal.png"
    );
    static final int TEX_SIZE = 256;
    static final int SPRITE_U = 0;
    static final int SPRITE_V = 220;
    static final int SPRITE_H = 16;

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
                TEXTURE,
                getX(),
                getY(),
                SPRITE_U,
                SPRITE_V + v,
                getWidth(),
                getHeight(),
                TEX_SIZE,
                TEX_SIZE
        );
    }

    @Override
    public void renderString(GuiGraphics graphics, Font font, int color) {
        Component label = getMessage().copy().withColor(LABEL_COLOR);
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
