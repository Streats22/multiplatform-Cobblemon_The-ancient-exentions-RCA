package nl.streats1.ancientextensions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import nl.streats1.ancientextensions.block.FieldSurveyMonitorBlockEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Matrix4f;

public class FieldSurveyMonitorRenderer implements BlockEntityRenderer<FieldSurveyMonitorBlockEntity> {

    private static final int TEXT_COLOR = 0xFF3D4F5C;
    private static final float SCALE = 0.0125F;

    public FieldSurveyMonitorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            FieldSurveyMonitorBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        if (!blockEntity.hasAnyText()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Font font = minecraft.font;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.02, 0.5);
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-SCALE, -SCALE, SCALE);

        Matrix4f matrix = poseStack.last().pose();
        int y = 0;
        for (int line = 0; line < 4; line++) {
            String text = blockEntity.line(line);
            if (text.isBlank()) {
                continue;
            }
            float width = font.width(text);
            font.drawInBatch(
                    text,
                    -width / 2.0F,
                    y,
                    TEXT_COLOR,
                    false,
                    matrix,
                    buffers,
                    Font.DisplayMode.POLYGON_OFFSET,
                    0,
                    packedLight
            );
            y += 10;
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(FieldSurveyMonitorBlockEntity blockEntity) {
        return blockEntity.hasAnyText();
    }
}
