package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.menu.FieldSurveyCalendarMenu;
import nl.streats1.ancientextensions.menu.FieldSurveyTabletMenu;
import nl.streats1.ancientextensions.menu.MigrationRouteChartMenu;
import nl.streats1.ancientextensions.menu.PokeballPouchMenu;
import nl.streats1.ancientextensions.menu.RegionalPassportMenu;
import nl.streats1.ancientextensions.menu.RegionalSurveyJournalMenu;
import nl.streats1.ancientextensions.registry.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class AncientExtensionsScreens {

    private AncientExtensionsScreens() {
    }

    public static void register(MenuScreenRegistrar registrar) {
        registrar.register(ModMenuTypes.POKEBALL_POUCH, PokeballPouchScreen::new);
        registrar.register(ModMenuTypes.REGIONAL_PASSPORT, RegionalPassportScreen::new);
        registrar.register(ModMenuTypes.REGIONAL_SURVEY_JOURNAL, RegionalSurveyJournalScreen::new);
        registrar.register(ModMenuTypes.MIGRATION_ROUTE_CHART, MigrationRouteChartScreen::new);
        registrar.register(ModMenuTypes.FIELD_SURVEY_TABLET, FieldSurveyTabletScreen::new);
        registrar.register(ModMenuTypes.FIELD_SURVEY_CALENDAR, FieldSurveyCalendarScreen::new);
    }

    @FunctionalInterface
    public interface MenuScreenRegistrar {
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                MenuType<? extends M> type,
                MenuScreens.ScreenConstructor<M, U> constructor
        );
    }
}
