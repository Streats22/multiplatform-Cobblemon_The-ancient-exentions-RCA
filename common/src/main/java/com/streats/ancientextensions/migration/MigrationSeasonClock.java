package com.streats.ancientextensions.migration;

import net.minecraft.server.level.ServerLevel;

/**
 * World calendar for migration seasons. Replace with Serene Seasons Plus when integrated on NeoForge.
 */
public final class MigrationSeasonClock {

    private MigrationSeasonClock() {
    }

    public static MigrationSeason currentSeason(ServerLevel level) {
        long day = level.getDayTime() / 24000L;
        long seasonIndex = (day / MigrationConfig.DAYS_PER_SEASON) % 4;
        return MigrationSeason.values()[(int) seasonIndex];
    }
}
