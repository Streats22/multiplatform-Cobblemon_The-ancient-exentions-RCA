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
    /** Soft parchment ink — two layers so white body text stays readable. */
    private static final int SOFT_SHADOW_INK = 0x60483828;
    private static final int SOFT_SHADOW_DEEP = 0x38241810;
    private static final int GOLD_HALO_INK = 0x58402818;

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

    public static void drawStyledSoft(Font font, GuiGraphics graphics, Component text, int x, int y) {
        drawSoftShadow(font, graphics, text, x, y);
        drawStyled(font, graphics, text, x, y, false);
    }

    public static void drawStyledSoft(Font font, GuiGraphics graphics, FormattedCharSequence text, int x, int y) {
        drawSoftShadow(font, graphics, text, x, y);
        drawStyled(font, graphics, text, x, y, false);
    }

    private static void drawSoftShadow(Font font, GuiGraphics graphics, Component text, int x, int y) {
        graphics.drawString(font, text, x + 2, y + 2, SOFT_SHADOW_DEEP, false);
        graphics.drawString(font, text, x + 1, y + 1, SOFT_SHADOW_INK, false);
    }

    private static void drawSoftShadow(Font font, GuiGraphics graphics, FormattedCharSequence text, int x, int y) {
        graphics.drawString(font, text, x + 2, y + 2, SOFT_SHADOW_DEEP, false);
        graphics.drawString(font, text, x + 1, y + 1, SOFT_SHADOW_INK, false);
    }

    public static void drawCenteredStyledSoft(Font font, GuiGraphics graphics, Component text, int centerX, int y) {
        drawStyledSoft(font, graphics, text, centerX - font.width(text) / 2, y);
    }

    public static void drawCenteredGold(Font font, GuiGraphics graphics, Component text, int centerX, int y, int goldColor) {
        Component colored = text.copy().withColor(goldColor);
        int x = centerX - font.width(colored) / 2;
        graphics.drawString(font, colored, x + 1, y + 1, GOLD_HALO_INK, false);
        drawStyled(font, graphics, colored, x, y, false);
    }

    public static void drawGold(Font font, GuiGraphics graphics, Component text, int x, int y, int goldColor) {
        Component colored = text.copy().withColor(goldColor);
        graphics.drawString(font, colored, x + 1, y + 1, GOLD_HALO_INK, false);
        drawStyled(font, graphics, colored, x, y, false);
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

    public static int drawWrappedSoft(Font font, GuiGraphics graphics, Component text, int x, int y, int maxWidth) {
        int lineY = y;
        for (FormattedCharSequence line : font.split(text, maxWidth)) {
            drawStyledSoft(font, graphics, line, x, lineY);
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

    public static int drawWrappedCenteredSoft(Font font, GuiGraphics graphics, Component text, int centerX, int y, int maxWidth) {
        int lineY = y;
        for (FormattedCharSequence line : font.split(text, maxWidth)) {
            int lineW = font.width(line);
            drawStyledSoft(font, graphics, line, centerX - lineW / 2, lineY);
            lineY += 10;
        }
        return lineY;
    }
}
