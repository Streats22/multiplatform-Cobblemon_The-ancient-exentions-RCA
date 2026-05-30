package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Animated wax seal with region badge code.
 */
public final class PassportSealRenderer {

    public static final int SEAL_SIZE = 44;

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientExtensionsConstants.MOD_ID,
            "textures/gui/regional_passport.png"
    );
    public static final int WAX_U = 0;
    public static final int WAX_V = 200;
    private static final int WAX_W = 48;
    private static final int WAX_H = 48;

    private static final int WAX_RED = 0xFFB03838;
    private static final int GOLD = 0xFFD4AF37;
    private static final int GOLD_HI = 0xFFF0D070;

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
        int accent = region.nameColor().getColor() != null ? region.nameColor().getColor() : 0xFFC04040;
        float eased = easeOutBack(Mth.clamp(stampProgress, 0.0F, 1.0F));
        float settlePulse = stampProgress >= 1.0F
                ? 1.0F + 0.02F * Mth.sin((gameTime + partialTick) * 0.14F)
                : 1.0F;
        float scale = (0.4F + 0.6F * eased) * settlePulse;

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);

        drawGoldRing(graphics, eased);

        float waxAlpha = 0.55F + 0.45F * eased;
        graphics.setColor(
                ((WAX_RED >> 16) & 0xFF) / 255.0F,
                ((WAX_RED >> 8) & 0xFF) / 255.0F,
                (WAX_RED & 0xFF) / 255.0F,
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

        drawAccentRing(graphics, accent, eased);

        String code = region.getBadgeCode();
        int codeWidth = font.width(code);
        graphics.drawString(font, code, -codeWidth / 2 + 1, -4, 0xFF502020, false);
        graphics.drawString(font, code, -codeWidth / 2, -5, 0xFFFFF0D0, false);

        graphics.pose().popPose();
    }

    private static void drawGoldRing(GuiGraphics graphics, float eased) {
        int alpha = (int) (180 + 75 * eased);
        drawCircleOutline(graphics, 0, 0, 22, (alpha << 24) | (GOLD & 0xFFFFFF));
        drawCircleOutline(graphics, 0, 0, 21, (alpha << 24) | (GOLD_HI & 0xFFFFFF));
    }

    private static void drawAccentRing(GuiGraphics graphics, int accent, float eased) {
        int alpha = (int) (90 * eased);
        int colour = (alpha << 24) | (accent & 0xFFFFFF);
        for (int r = 17; r <= 18; r++) {
            drawCircleOutline(graphics, 0, 0, r, colour);
        }
    }

    private static void drawCircleOutline(GuiGraphics graphics, int cx, int cy, int radius, int colour) {
        for (int deg = 0; deg < 360; deg += 6) {
            float rad = (float) Math.toRadians(deg);
            int x = cx + (int) (Mth.cos(rad) * radius);
            int y = cy + (int) (Mth.sin(rad) * radius);
            graphics.fill(x, y, x + 1, y + 1, colour);
        }
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        float u = t - 1.0F;
        return 1.0F + c3 * u * u * u + c1 * u * u;
    }
}
