package nl.streats1.ancientextensions.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

/**
 * Draws the holder's skin face inside the passport photo frame.
 */
public final class PassportFaceRenderer {

    private static final int BACKDROP = 0xFF2A2018;
    private static final int INNER = 0xFFEBE0C8;
    private static final int BORDER = 0xFFC9A227;

    private PassportFaceRenderer() {
    }

    public static void draw(GuiGraphics graphics, int x, int y, int size) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }

        graphics.fill(x - 1, y - 1, x + size + 1, y + size + 1, BORDER);
        graphics.fill(x, y, x + size, y + size, BACKDROP);
        graphics.fill(x + 1, y + 1, x + size - 1, y + size - 1, INNER);

        PlayerSkin skin = clientPlayer.getSkin();
        ResourceLocation texture = skin.texture();
        int inset = 2;
        int inner = size - inset * 2;
        graphics.blit(texture, x + inset, y + inset, inner, inner, 8.0F, 8.0F, 8, 8, 64, 64);
        graphics.blit(texture, x + inset, y + inset, inner, inner, 40.0F, 8.0F, 8, 8, 64, 64);
    }
}
