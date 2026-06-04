package nl.streats1.ancientextensions.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import nl.streats1.ancientextensions.registry.ModMenuTypes;

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
