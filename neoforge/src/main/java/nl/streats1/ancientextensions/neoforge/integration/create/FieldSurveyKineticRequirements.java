package nl.streats1.ancientextensions.neoforge.integration.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Method;

import nl.streats1.ancientextensions.field.FieldSurveyStack;

public final class FieldSurveyKineticRequirements {

    public static final float REQUIRED_RPM = 32.0F;

    private static final String KINETIC_BE_CLASS =
            "com.simibubi.create.content.kinetics.base.KineticBlockEntity";

    private static Class<?> kineticBlockEntityClass;
    private static Method getSpeedMethod;

    private FieldSurveyKineticRequirements() {
    }

    public static boolean hasShaftPowerFromBelow(Level level, BlockPos sensorPos) {
        return hasRequiredSpeed(level.getBlockEntity(sensorPos.below()));
    }

    public static boolean isSourceReady(DisplayLinkContext context) {
        Level level = context.level();
        BlockPos pos = context.getSourcePos();
        return FieldSurveyStack.hasMonitorAbove(level, pos) && hasShaftPowerFromBelow(level, pos);
    }

    public static MutableComponent rpmRequiredLine() {
        return Component.translatable(
                "ancient_extensions.display_source.rpm_required",
                (int) REQUIRED_RPM
        );
    }

    public static MutableComponent stackRequiredLine() {
        return Component.translatable("ancient_extensions.display_source.stack_required");
    }

    private static boolean hasRequiredSpeed(BlockEntity blockEntity) {
        if (blockEntity == null) {
            return false;
        }
        try {
            Class<?> kineticClass = kineticClass();
            if (!kineticClass.isInstance(blockEntity)) {
                return false;
            }
            float speed = (float) speedMethod().invoke(blockEntity);
            return Math.abs(speed) >= REQUIRED_RPM;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static Class<?> kineticClass() throws ClassNotFoundException {
        if (kineticBlockEntityClass == null) {
            kineticBlockEntityClass = Class.forName(KINETIC_BE_CLASS);
        }
        return kineticBlockEntityClass;
    }

    private static Method speedMethod() throws ReflectiveOperationException {
        if (getSpeedMethod == null) {
            getSpeedMethod = kineticClass().getMethod("getSpeed");
        }
        return getSpeedMethod;
    }
}
