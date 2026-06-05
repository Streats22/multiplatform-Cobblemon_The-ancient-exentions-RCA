package nl.streats1.ancientextensions.field;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import nl.streats1.ancientextensions.block.FieldSurveyMonitorBlockEntity;
import nl.streats1.ancientextensions.migration.MigrationBiomeLocator;
import nl.streats1.ancientextensions.migration.MigrationRouteTarget;

public final class FieldSurveyStackLines {

    private FieldSurveyStackLines() {
    }

    public static void refreshMonitor(ServerLevel level, BlockPos sensorPos, FieldSurveyMonitorBlockEntity monitor) {
        FieldSurveyWorldSnapshot snapshot = FieldSurveyWorldSnapshot.at(level, sensorPos);
        MigrationRouteTarget bearing = MigrationBiomeLocator.resolveNear(level, sensorPos);
        monitor.setLineText(0, snapshot.seasonLine());
        monitor.setLineText(1, snapshot.biomeRouteLine());
        monitor.setLineText(2, snapshot.speciesLine());
        monitor.setLineText(3, formatBearing(bearing));
        monitor.setChanged();
    }

    public static String formatBearing(MigrationRouteTarget target) {
        return switch (target.state()) {
            case ON_ROUTE -> "Here · L" + target.legDisplay() + "/" + target.legCount()
                    + " · " + target.catchesOnLeg() + "/" + target.catchesRequired();
            case SEEKING_BIOME -> target.distanceBlocks() + "m " + target.bearingLabel()
                    + " · L" + target.legDisplay() + " · " + target.biomeLabel();
            case ROUTE_COMPLETE -> "Route complete";
            case NOT_FOUND -> "No biome in range · L" + target.legDisplay();
        };
    }
}
