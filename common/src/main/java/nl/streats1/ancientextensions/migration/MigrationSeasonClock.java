package nl.streats1.ancientextensions.migration;

import net.minecraft.server.level.ServerLevel;

import nl.streats1.ancientextensions.integration.sereneseasons.SereneSeasonsIntegration;

/**
 * World calendar for migration seasons. Uses Serene Seasons when installed; otherwise 7 in-game days per season.
 */
public final class MigrationSeasonClock {

    private MigrationSeasonClock() {
    }

    public static MigrationSeason currentSeason(ServerLevel level) {
        return SereneSeasonsIntegration.currentSeason(level)
                .orElseGet(() -> internalSeason(level));
    }

    public static MigrationCalendarSource calendarSource() {
        return SereneSeasonsIntegration.useSereneSeasonsCalendar()
                ? MigrationCalendarSource.SERENE_SEASONS
                : MigrationCalendarSource.INTERNAL_DAYS;
    }

    private static MigrationSeason internalSeason(ServerLevel level) {
        long day = level.getDayTime() / 24000L;
        long seasonIndex = (day / MigrationConfig.DAYS_PER_SEASON) % 4;
        return MigrationSeason.values()[(int) seasonIndex];
    }
}
