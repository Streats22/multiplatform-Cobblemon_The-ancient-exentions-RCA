package nl.streats1.ancientextensions.field;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class FieldSurveyPower {

    @FunctionalInterface
    public interface Check {
        boolean hasShaftPowerFromBelow(Level level, BlockPos sensorPos);
    }

    private static Check check = (level, sensorPos) -> false;

    private FieldSurveyPower() {
    }

    public static void register(Check powerCheck) {
        check = powerCheck;
    }

    public static boolean hasShaftPowerFromBelow(Level level, BlockPos sensorPos) {
        return check.hasShaftPowerFromBelow(level, sensorPos);
    }
}
