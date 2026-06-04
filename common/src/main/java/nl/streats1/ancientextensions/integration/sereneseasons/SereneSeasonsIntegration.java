package nl.streats1.ancientextensions.integration.sereneseasons;

import nl.streats1.ancientextensions.config.MigrationCalendarConfig;
import nl.streats1.ancientextensions.migration.MigrationSeason;
import nl.streats1.ancientextensions.util.ModPresence;
import net.minecraft.server.level.ServerLevel;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;

/**
 * Optional calendar from Serene Seasons / Serene Seasons Plus ({@code sereneseasons}).
 */
public final class SereneSeasonsIntegration {

    public static final String MOD_ID = "sereneseasons";

    private static final String SEASON_HELPER = "sereneseasons.api.season.SeasonHelper";

    private static Method getSeasonState;
    private static Method getSeason;
    private static Method getTropicalSeason;

    private SereneSeasonsIntegration() {
    }

    public static boolean isLoaded() {
        return ModPresence.isLoaded(MOD_ID);
    }

    public static boolean useSereneSeasonsCalendar() {
        return isLoaded() && MigrationCalendarConfig.useSereneSeasonsWhenPresent();
    }

    public static Optional<MigrationSeason> currentSeason(ServerLevel level) {
        if (!useSereneSeasonsCalendar()) {
            return Optional.empty();
        }
        try {
            ensureReflection();
            if (getSeasonState == null) {
                return Optional.empty();
            }
            Object state = getSeasonState.invoke(null, level);
            if (state == null) {
                return Optional.empty();
            }
            if (getSeason != null) {
                Object season = getSeason.invoke(state);
                Optional<MigrationSeason> mapped = mapTemperateSeason(season);
                if (mapped.isPresent()) {
                    return mapped;
                }
            }
            if (getTropicalSeason != null) {
                Object tropical = getTropicalSeason.invoke(state);
                return mapTropicalSeason(tropical);
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return Optional.empty();
    }

    private static Optional<MigrationSeason> mapTemperateSeason(Object season) {
        if (season == null) {
            return Optional.empty();
        }
        String name = season.toString();
        try {
            return Optional.of(MigrationSeason.valueOf(name));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private static Optional<MigrationSeason> mapTropicalSeason(Object tropical) {
        if (tropical == null) {
            return Optional.empty();
        }
        String name = tropical.toString().toUpperCase(Locale.ROOT);
        if (name.contains("WET")) {
            return Optional.of(MigrationSeason.SPRING);
        }
        if (name.contains("DRY")) {
            return Optional.of(MigrationSeason.SUMMER);
        }
        return Optional.empty();
    }

    private static void ensureReflection() throws ReflectiveOperationException {
        if (getSeasonState != null) {
            return;
        }
        Class<?> helper = Class.forName(SEASON_HELPER);
        getSeasonState = helper.getMethod("getSeasonState", net.minecraft.world.level.Level.class);
        Class<?> stateClass = Class.forName("sereneseasons.api.season.ISeasonState");
        getSeason = stateClass.getMethod("getSeason");
        try {
            getTropicalSeason = stateClass.getMethod("getTropicalSeason");
        } catch (NoSuchMethodException ignored) {
            getTropicalSeason = null;
        }
    }
}
