package nl.streats1.ancientextensions.registry;

import net.minecraft.world.inventory.MenuType;

import nl.streats1.ancientextensions.menu.*;

/**
 * Populated by each platform loader when menu types are registered.
 */
public final class ModMenuTypes {

    public static MenuType<RegionalPassportMenu> REGIONAL_PASSPORT;
    public static MenuType<RegionalSurveyJournalMenu> REGIONAL_SURVEY_JOURNAL;
    public static MenuType<MigrationRouteChartMenu> MIGRATION_ROUTE_CHART;
    public static MenuType<FieldSurveyTabletMenu> FIELD_SURVEY_TABLET;
    public static MenuType<PokeballPouchMenu> POKEBALL_POUCH;
    public static MenuType<FieldSurveyCalendarMenu> FIELD_SURVEY_CALENDAR;

    private ModMenuTypes() {
    }
}
