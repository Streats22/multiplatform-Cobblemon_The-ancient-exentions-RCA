package nl.streats1.ancientextensions.config;

/**
 * Runtime migration calendar settings (loaded from platform config on startup).
 */
public final class MigrationCalendarConfig {

    private static boolean useSereneSeasonsWhenPresent = true;

    private MigrationCalendarConfig() {
    }

    /**
     * When Serene Seasons is installed, use its season cycle instead of the 7-day internal calendar.
     */
    public static boolean useSereneSeasonsWhenPresent() {
        return useSereneSeasonsWhenPresent;
    }

    public static void apply(boolean useSereneSeasons) {
        useSereneSeasonsWhenPresent = useSereneSeasons;
    }
}
