package com.streats.ancientextensions.neoforge.client;

import com.streats.ancientextensions.AncientExtensionsConstants;
import com.streats.ancientextensions.dex.SurveyRegion;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Animated wax seal with region code — scale-in stamp, then a gentle pulse.
 */
public final class PassportSealRenderer {

    public static final int SEAL_SIZE = 52;

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientExtensionsConstants.MOD_ID,
            "textures/gui/regional_passport.png"
    );
    /** Wax blob sprite on the GUI sheet (below main panel). */
    public static final int WAX_U = 0;
    public static final int WAX_V = 200;
    private static final int WAX_W = 48;
    private static final int WAX_H = 48;
    /** Embossed ring on the GUI sheet. */
    private static final int RING_U = 48;
    private static final int RING_V = 200;
    private static final int RING_W = 56;
    private static final int RING_H = 56;

    private PassportSealRenderer() {
    }

    public static void draw(
            GuiGraphics graphics,
            Font font,
            SurveyRegion region,
            int centerX,
            int centerY,
            float partialTick,
            float stampProgress,
            long gameTime
    ) {
        int stampColor = region.nameColor().getColor() != null ? region.nameColor().getColor() : 0xC04040;
        float eased = easeOutBack(Mth.clamp(stampProgress, 0.0F, 1.0F));
        float settlePulse = stampProgress >= 1.0F
                ? 1.0F + 0.035F * Mth.sin((gameTime + partialTick) * 0.14F)
                : 1.0F;
        float scale = (0.35F + 0.65F * eased) * settlePulse;
        float wobble = stampProgress < 1.0F
                ? (1.0F - eased) * 0.12F * Mth.sin(eased * (float) Math.PI * 6.0F)
                : 0.0F;

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotation(wobble));

        int half = SEAL_SIZE / 2;
        int ringX = -RING_W / 2;
        int ringY = -RING_H / 2;
        graphics.setColor(1.0F, 1.0F, 1.0F, 0.55F + 0.45F * eased);
        graphics.blit(
                TEXTURE,
                ringX,
                ringY,
                RING_U,
                RING_V,
                RING_W,
                RING_H,
                256,
                256
        );

        float waxAlpha = 0.4F + 0.6F * eased;
        int waxTint = blendTowardWhite(stampColor, 0.25F);
        graphics.setColor(
                ((waxTint >> 16) & 0xFF) / 255.0F,
                ((waxTint >> 8) & 0xFF) / 255.0F,
                (waxTint & 0xFF) / 255.0F,
                waxAlpha
        );
        graphics.blit(
                TEXTURE,
                -WAX_W / 2,
                -WAX_H / 2,
                WAX_U,
                WAX_V,
                WAX_W,
                WAX_H,
                256,
                256
        );
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (eased > 0.15F) {
            drawWaxDrips(graphics, stampColor, eased, gameTime, partialTick);
        }

        String code = region.getBadgeCode();
        float textScale = 1.55F;
        graphics.pose().pushPose();
        graphics.pose().scale(textScale, textScale, 1.0F);
        int codeWidth = font.width(code);
        int dark = darken(stampColor, 0.45F);
        graphics.drawString(font, code, -codeWidth / 2 + 1, -3, dark, false);
        graphics.drawString(font, code, -codeWidth / 2, -4, 0xFFF8E8, false);
        graphics.pose().popPose();

        graphics.pose().popPose();
    }

    private static void drawWaxDrips(
            GuiGraphics graphics,
            int color,
            float eased,
            long gameTime,
            float partialTick
    ) {
        float dripPhase = (gameTime + partialTick) * 0.08F;
        int dripColor = blendTowardWhite(color, 0.1F);
        for (int i = 0; i < 5; i++) {
            float angle = (float) (i * Math.PI * 2.0 / 5.0 + dripPhase);
            int radius = 16 + (i % 2);
            int dx = (int) (Mth.cos(angle) * radius);
            int dy = (int) (Mth.sin(angle) * radius * 0.85F) + 8;
            int size = 3 + (i % 2);
            graphics.fill(dx - size / 2, dy, dx + size / 2 + 1, dy + size + 2, withAlpha(dripColor, (int) (180 * eased)));
        }
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        float u = t - 1.0F;
        return 1.0F + c3 * u * u * u + c1 * u * u;
    }

    private static int blendTowardWhite(int rgb, float amount) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int nr = (int) (r + (255 - r) * amount);
        int ng = (int) (g + (255 - g) * amount);
        int nb = (int) (b + (255 - b) * amount);
        return (nr << 16) | (ng << 8) | nb;
    }

    private static int darken(int rgb, float factor) {
        int r = (int) (((rgb >> 16) & 0xFF) * factor);
        int g = (int) (((rgb >> 8) & 0xFF) * factor);
        int b = (int) ((rgb & 0xFF) * factor);
        return (r << 16) | (g << 8) | b;
    }

    private static int withAlpha(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }
}
