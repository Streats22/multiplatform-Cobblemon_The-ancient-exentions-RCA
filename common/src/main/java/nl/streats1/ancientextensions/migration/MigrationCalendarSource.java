package nl.streats1.ancientextensions.migration;

import net.minecraft.network.chat.Component;

/**
 * Which calendar drives {@link MigrationSeasonClock}.
 */
public enum MigrationCalendarSource {
    SERENE_SEASONS("ancient_extensions.migration.calendar.serene_seasons"),
    INTERNAL_DAYS("ancient_extensions.migration.calendar.internal_days");

    private final String translationKey;

    MigrationCalendarSource(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component label() {
        return Component.translatable(translationKey);
    }
}
