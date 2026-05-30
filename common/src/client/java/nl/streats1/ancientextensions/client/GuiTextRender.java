package nl.streats1.ancientextensions.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/**
 * Draws {@link Component} text without flattening {@link net.minecraft.ChatFormatting} colors.
 */
public final class GuiTextRender {

    /** Default tint — white so per-glyph styles show at full strength. */
    private static final int STYLE_PRESERVING_COLOR = 0xFFFFFFFF;

    private GuiTextRender() {
    }

    public static void drawStyled(Font font, GuiGraphics graphics, Component text, int x, int y) {
        drawStyled(font, graphics, text, x, y, true);
    }

    public static void drawStyled(Font font, GuiGraphics graphics, FormattedCharSequence text, int x, int y) {
        drawStyled(font, graphics, text, x, y, true);
    }

    public static void drawStyled(Font font, GuiGraphics graphics, Component text, int x, int y, boolean shadow) {
        graphics.drawString(font, text, x, y, STYLE_PRESERVING_COLOR, shadow);
    }

    public static void drawStyled(Font font, GuiGraphics graphics, FormattedCharSequence text, int x, int y, boolean shadow) {
        graphics.drawString(font, text, x, y, STYLE_PRESERVING_COLOR, shadow);
    }

    public static void drawCenteredStyled(Font font, GuiGraphics graphics, Component text, int centerX, int y, boolean shadow) {
        drawStyled(font, graphics, text, centerX - font.width(text) / 2, y, shadow);
    }

    public static int drawWrapped(Font font, GuiGraphics graphics, Component text, int x, int y, int maxWidth) {
        return drawWrapped(font, graphics, text, x, y, maxWidth, true);
    }

    public static int drawWrapped(Font font, GuiGraphics graphics, Component text, int x, int y, int maxWidth, boolean shadow) {
        int lineY = y;
        for (FormattedCharSequence line : font.split(text, maxWidth)) {
            drawStyled(font, graphics, line, x, lineY, shadow);
            lineY += 10;
        }
        return lineY;
    }

    public static int drawWrappedCentered(Font font, GuiGraphics graphics, Component text, int centerX, int y, int maxWidth) {
        return drawWrappedCentered(font, graphics, text, centerX, y, maxWidth, true);
    }

    public static int drawWrappedCentered(Font font, GuiGraphics graphics, Component text, int centerX, int y, int maxWidth, boolean shadow) {
        int lineY = y;
        for (FormattedCharSequence line : font.split(text, maxWidth)) {
            int lineW = font.width(line);
            drawStyled(font, graphics, line, centerX - lineW / 2, lineY, shadow);
            lineY += 10;
        }
        return lineY;
    }
}
