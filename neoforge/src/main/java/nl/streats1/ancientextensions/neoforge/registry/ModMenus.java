package nl.streats1.ancientextensions.neoforge.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.menu.*;
import nl.streats1.ancientextensions.registry.ModMenuTypes;

public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, AncientExtensionsConstants.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<PokeballPouchMenu>> POKEBALL_POUCH = MENUS.register(
            "pokeball_pouch",
            () -> IMenuTypeExtension.create(PokeballPouchMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<RegionalPassportMenu>> REGIONAL_PASSPORT = MENUS.register(
            "regional_passport",
            () -> IMenuTypeExtension.create(RegionalPassportMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<RegionalSurveyJournalMenu>> REGIONAL_SURVEY_JOURNAL = MENUS.register(
            "regional_survey_journal",
            () -> IMenuTypeExtension.create(RegionalSurveyJournalMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<MigrationRouteChartMenu>> MIGRATION_ROUTE_CHART = MENUS.register(
            "migration_route_chart",
            () -> IMenuTypeExtension.create(MigrationRouteChartMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<FieldSurveyTabletMenu>> FIELD_SURVEY_TABLET = MENUS.register(
            "field_survey_tablet",
            () -> IMenuTypeExtension.create(FieldSurveyTabletMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<FieldSurveyCalendarMenu>> FIELD_SURVEY_CALENDAR = MENUS.register(
            "field_survey_calendar",
            () -> IMenuTypeExtension.create(FieldSurveyCalendarMenu::new)
    );

    private ModMenus() {
    }

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
        modBus.addListener(ModMenus::onRegister);
    }

    private static void onRegister(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.MENU)) {
            ModMenuTypes.POKEBALL_POUCH = POKEBALL_POUCH.get();
            ModMenuTypes.REGIONAL_PASSPORT = REGIONAL_PASSPORT.get();
            ModMenuTypes.REGIONAL_SURVEY_JOURNAL = REGIONAL_SURVEY_JOURNAL.get();
            ModMenuTypes.MIGRATION_ROUTE_CHART = MIGRATION_ROUTE_CHART.get();
            ModMenuTypes.FIELD_SURVEY_TABLET = FIELD_SURVEY_TABLET.get();
            ModMenuTypes.FIELD_SURVEY_CALENDAR = FIELD_SURVEY_CALENDAR.get();
        }
    }
}
