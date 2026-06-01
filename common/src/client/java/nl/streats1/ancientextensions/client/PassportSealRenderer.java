package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Animated wax seal with region badge code.
 */
public final class PassportSealRenderer {

    public static final int SEAL_SIZE = 56;

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientExtensionsConstants.MOD_ID,
            "textures/gui/regional_passport.png"
    );
    private static final int SEAL_U = 0;
    private static final int SEAL_V = 200;
    private static final int SEAL_W = 56;
    private static final int SEAL_H = 56;
    private static final int TEX_SIZE = 256;

    private static final int CODE_GOLD = 0xFFF5E6B0;
    private static final int CODE_SHADOW = 0x50281818;
    private static final float STAMP_ROTATION_DEG = -11.0F;

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
        float eased = easeOutBack(Mth.clamp(stampProgress, 0.0F, 1.0F));
        float settlePulse = stampProgress >= 1.0F
                ? 1.0F + 0.015F * Mth.sin((gameTime + partialTick) * 0.12F)
                : 1.0F;
        float scale = (0.35F + 0.65F * eased) * settlePulse;
        float alpha = Mth.clamp(0.45F + 0.55F * eased, 0.0F, 1.0F);

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(STAMP_ROTATION_DEG));

        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        graphics.blit(
                TEXTURE,
                -SEAL_W / 2,
                -SEAL_H / 2,
                SEAL_U,
                SEAL_V,
                SEAL_W,
                SEAL_H,
                TEX_SIZE,
                TEX_SIZE
        );
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int accent = region.nameColor().getColor() != null ? region.nameColor().getColor() : 0xFFC04040;
        drawRegionAccentRing(graphics, accent, eased);

        String code = region.getBadgeCode();
        Component label = Component.literal(code).withStyle(net.minecraft.ChatFormatting.BOLD);
        int codeWidth = font.width(label);
        int textAlpha = (int) (255 * alpha);
        int shadowColor = (textAlpha << 24) | (CODE_SHADOW & 0xFFFFFF);
        int goldColor = (textAlpha << 24) | (CODE_GOLD & 0xFFFFFF);
        graphics.drawString(font, label, -codeWidth / 2 + 1, -3, shadowColor, false);
        graphics.drawString(font, label, -codeWidth / 2, -4, goldColor, false);

        graphics.pose().popPose();
    }

    /** Thin region-colored ring over the embossed gold rim. */
    private static void drawRegionAccentRing(GuiGraphics graphics, int accent, float eased) {
        int alpha = (int) (70 * eased);
        if (alpha <= 0) {
            return;
        }
        int colour = (alpha << 24) | (accent & 0xFFFFFF);
        int inner = 16;
        int outer = 19;
        for (int y = -outer; y <= outer; y++) {
            for (int x = -outer; x <= outer; x++) {
                int distSq = x * x + y * y;
                if (distSq <= outer * outer && distSq >= inner * inner) {
                    graphics.fill(x, y, x + 1, y + 1, colour);
                }
            }
        }
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        float u = t - 1.0F;
        return 1.0F + c3 * u * u * u + c1 * u * u;
    }
}
