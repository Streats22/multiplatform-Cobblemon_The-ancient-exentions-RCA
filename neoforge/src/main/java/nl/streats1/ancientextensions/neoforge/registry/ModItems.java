package nl.streats1.ancientextensions.neoforge.registry;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.item.AncientProfessorsKitItem;
import nl.streats1.ancientextensions.item.MigrationRouteChartItem;
import nl.streats1.ancientextensions.item.PokeballPouchItem;
import nl.streats1.ancientextensions.item.RegionalPassportItem;
import nl.streats1.ancientextensions.item.RegionalSurveyJournalItem;
import nl.streats1.ancientextensions.registry.ModContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {

    private static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(AncientExtensionsConstants.MOD_ID);

    public static final DeferredItem<Item> ANCIENT_PROFESSORS_KIT = ITEMS.register(
            "ancient_professors_kit",
            () -> new AncientProfessorsKitItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant())
    );

    public static final DeferredItem<Item> REGIONAL_SURVEY_JOURNAL = ITEMS.register(
            "regional_survey_journal",
            () -> new RegionalSurveyJournalItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
    );

    public static final DeferredItem<Item> MIGRATION_ROUTE_CHART = ITEMS.register(
            "migration_route_chart",
            () -> new MigrationRouteChartItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON))
    );

    public static final DeferredItem<Item> REGIONAL_PASSPORT = ITEMS.register(
            "regional_passport",
            () -> new RegionalPassportItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
    );

    public static final DeferredItem<Item> POKEBALL_POUCH = ITEMS.register(
            "pokeball_pouch",
            () -> new PokeballPouchItem(
                    ModBlocks.POKEBALL_POUCH.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
            )
    );

    private ModItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        modBus.addListener(ModItems::onRegister);
    }

    private static void onRegister(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            ModContent.ANCIENT_PROFESSORS_KIT = ANCIENT_PROFESSORS_KIT.get();
            ModContent.REGIONAL_SURVEY_JOURNAL = REGIONAL_SURVEY_JOURNAL.get();
            ModContent.MIGRATION_ROUTE_CHART = MIGRATION_ROUTE_CHART.get();
            ModContent.REGIONAL_PASSPORT = REGIONAL_PASSPORT.get();
            ModContent.POKEBALL_POUCH = POKEBALL_POUCH.get();
        }
    }
}
