package nl.streats1.ancientextensions.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Passport picker control — flat label text without the default Minecraft shadow.
 */
public class FlatTextButton extends Button {

    public FlatTextButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderString(GuiGraphics graphics, Font font, int color) {
        GuiTextRender.drawCenteredStyled(
                font,
                graphics,
                getMessage(),
                getX() + getWidth() / 2,
                getY() + (getHeight() - 8) / 2,
                false
        );
    }
}
