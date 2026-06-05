package nl.streats1.ancientextensions.field;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import nl.streats1.ancientextensions.registry.ModContent;

public final class FieldSurveyStack {

    private FieldSurveyStack() {
    }

    public static boolean isSensor(Block block) {
        return block == ModContent.FIELD_SURVEY_SENSOR_BLOCK;
    }

    public static boolean isMonitor(Block block) {
        return block == ModContent.FIELD_SURVEY_MONITOR_BLOCK;
    }

    public static boolean hasMonitorAbove(Level level, BlockPos sensorPos) {
        return isMonitor(level.getBlockState(sensorPos.above()).getBlock());
    }

    public static boolean isMountedOnSensor(Level level, BlockPos monitorPos) {
        return isSensor(level.getBlockState(monitorPos.below()).getBlock());
    }

    public static boolean isAssemblyReady(Level level, BlockPos sensorPos) {
        return hasMonitorAbove(level, sensorPos) && FieldSurveyPower.hasShaftPowerFromBelow(level, sensorPos);
    }

    public static boolean canPlaceMonitorOn(BlockState support, Direction face) {
        return face == Direction.UP && isSensor(support.getBlock());
    }
}
