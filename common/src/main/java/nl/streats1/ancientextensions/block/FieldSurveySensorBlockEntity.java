package nl.streats1.ancientextensions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import nl.streats1.ancientextensions.field.FieldSurveyPower;
import nl.streats1.ancientextensions.field.FieldSurveyStack;
import nl.streats1.ancientextensions.field.FieldSurveyStackLines;
import nl.streats1.ancientextensions.registry.ModContent;

public class FieldSurveySensorBlockEntity extends BlockEntity {

    private static final int REFRESH_INTERVAL = 40;

    public FieldSurveySensorBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.FIELD_SURVEY_SENSOR_BE, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide() || level.getGameTime() % REFRESH_INTERVAL != 0) {
            return;
        }
        if (!FieldSurveyPower.hasShaftPowerFromBelow(level, pos) || !FieldSurveyStack.hasMonitorAbove(level, pos)) {
            return;
        }
        BlockEntity above = level.getBlockEntity(pos.above());
        if (!(above instanceof FieldSurveyMonitorBlockEntity monitor) || !(level instanceof ServerLevel server)) {
            return;
        }
        FieldSurveyStackLines.refreshMonitor(server, pos, monitor);
        level.sendBlockUpdated(pos.above(), above.getBlockState(), above.getBlockState(), 3);
    }
}
