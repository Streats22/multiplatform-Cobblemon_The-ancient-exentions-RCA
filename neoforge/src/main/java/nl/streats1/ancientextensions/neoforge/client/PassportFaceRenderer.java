package nl.streats1.ancientextensions.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

/**
 * Draws the holder's skin face inside the passport photo frame.
 */
public final class PassportFaceRenderer {

    private PassportFaceRenderer() {
    }

    public static void draw(GuiGraphics graphics, int x, int y, int size) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }
        PlayerSkin skin = clientPlayer.getSkin();
        ResourceLocation texture = skin.texture();
        graphics.blit(texture, x, y, 8.0F, 8.0F, size, size, 64, 64);
        graphics.blit(texture, x, y, 40.0F, 8.0F, size, size, 64, 64);
    }
}
