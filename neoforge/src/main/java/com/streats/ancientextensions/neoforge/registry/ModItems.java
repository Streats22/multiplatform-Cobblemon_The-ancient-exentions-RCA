package com.streats.ancientextensions.neoforge.registry;

import com.streats.ancientextensions.AncientExtensionsConstants;
import com.streats.ancientextensions.neoforge.item.AncientProfessorsKitItem;
import com.streats.ancientextensions.neoforge.item.PokeballPouchItem;
import com.streats.ancientextensions.neoforge.item.RegionalPassportItem;
import com.streats.ancientextensions.neoforge.item.RegionalSurveyJournalItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

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
    }
}
