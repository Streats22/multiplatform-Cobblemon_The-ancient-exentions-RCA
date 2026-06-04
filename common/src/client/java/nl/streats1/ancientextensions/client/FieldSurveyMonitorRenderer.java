package nl.streats1.ancientextensions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import org.joml.Matrix4f;

import nl.streats1.ancientextensions.block.FieldSurveyMonitorBlock;
import nl.streats1.ancientextensions.block.FieldSurveyMonitorBlockEntity;

public class FieldSurveyMonitorRenderer implements BlockEntityRenderer<FieldSurveyMonitorBlockEntity> {

    private static final int TEXT_COLOR = 0xFF9AD4E8;
    private static final float SCALE = 0.011F;

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

        Direction facing = blockEntity.getBlockState().getValue(FieldSurveyMonitorBlock.FACING);
        var font = net.minecraft.client.Minecraft.getInstance().font;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.55, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0.0, 0.0, -0.505);
        poseStack.scale(-SCALE, -SCALE, SCALE);

        Matrix4f matrix = poseStack.last().pose();
        int y = -18;
        for (int line = 0; line < 4; line++) {
            String text = blockEntity.line(line);
            if (text.isBlank()) {
                continue;
            }
            Component component = Component.literal(text);
            float width = font.width(component);
            font.drawInBatch(
                    component,
                    -width / 2.0F,
                    y,
                    TEXT_COLOR,
                    false,
                    matrix,
                    buffers,
                    net.minecraft.client.gui.Font.DisplayMode.POLYGON_OFFSET,
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
