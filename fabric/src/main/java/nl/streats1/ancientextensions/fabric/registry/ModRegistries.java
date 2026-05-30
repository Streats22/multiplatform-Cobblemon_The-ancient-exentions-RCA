package nl.streats1.ancientextensions.fabric.registry;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.block.PokeballPouchBlock;
import nl.streats1.ancientextensions.block.PokeballPouchBlockEntity;
import nl.streats1.ancientextensions.item.AncientProfessorsKitItem;
import nl.streats1.ancientextensions.item.PokeballPouchItem;
import nl.streats1.ancientextensions.item.RegionalPassportItem;
import nl.streats1.ancientextensions.item.RegionalSurveyJournalItem;
import nl.streats1.ancientextensions.menu.PokeballPouchMenu;
import nl.streats1.ancientextensions.menu.RegionalPassportMenu;
import nl.streats1.ancientextensions.menu.RegionalSurveyJournalMenu;
import nl.streats1.ancientextensions.menu.sync.JournalOpenData;
import nl.streats1.ancientextensions.menu.sync.PassportOpenData;
import nl.streats1.ancientextensions.menu.sync.PouchOpenData;
import nl.streats1.ancientextensions.recipe.PokeballPouchRecipe;
import nl.streats1.ancientextensions.registry.ModRecipeSerializers;
import nl.streats1.ancientextensions.registry.ModContent;
import nl.streats1.ancientextensions.registry.ModMenuTypes;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModRegistries {

    private ModRegistries() {
    }

    public static void register() {
        ModContent.POKEBALL_POUCH_BLOCK = Registry.register(
                BuiltInRegistries.BLOCK,
                id("pokeball_pouch"),
                new PokeballPouchBlock(
                        BlockBehaviour.Properties.of()
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .dynamicShape()
                )
        );

        ModContent.POKEBALL_POUCH_BE = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id("pokeball_pouch"),
                FabricBlockEntityTypeBuilder.create(
                        PokeballPouchBlockEntity::new,
                        ModContent.POKEBALL_POUCH_BLOCK
                ).build()
        );

        ModMenuTypes.POKEBALL_POUCH = Registry.register(
                BuiltInRegistries.MENU,
                id("pokeball_pouch"),
                new ExtendedScreenHandlerType<>(
                        (syncId, inv, data) -> new PokeballPouchMenu(syncId, inv, data),
                        PouchOpenData.STREAM_CODEC
                )
        );
        ModMenuTypes.REGIONAL_PASSPORT = Registry.register(
                BuiltInRegistries.MENU,
                id("regional_passport"),
                new ExtendedScreenHandlerType<>(
                        (syncId, inv, data) -> new RegionalPassportMenu(syncId, inv, data),
                        PassportOpenData.STREAM_CODEC
                )
        );
        ModMenuTypes.REGIONAL_SURVEY_JOURNAL = Registry.register(
                BuiltInRegistries.MENU,
                id("regional_survey_journal"),
                new ExtendedScreenHandlerType<>(
                        (syncId, inv, data) -> new RegionalSurveyJournalMenu(syncId, inv, data),
                        JournalOpenData.STREAM_CODEC
                )
        );

        ModContent.ANCIENT_PROFESSORS_KIT = Registry.register(
                BuiltInRegistries.ITEM,
                id("ancient_professors_kit"),
                new AncientProfessorsKitItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant())
        );
        ModContent.REGIONAL_SURVEY_JOURNAL = Registry.register(
                BuiltInRegistries.ITEM,
                id("regional_survey_journal"),
                new RegionalSurveyJournalItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
        );
        ModContent.REGIONAL_PASSPORT = Registry.register(
                BuiltInRegistries.ITEM,
                id("regional_passport"),
                new RegionalPassportItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
        );
        ModContent.POKEBALL_POUCH = Registry.register(
                BuiltInRegistries.ITEM,
                id("pokeball_pouch"),
                new PokeballPouchItem(
                        ModContent.POKEBALL_POUCH_BLOCK,
                        new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
                )
        );

        ModRecipeSerializers.POKEBALL_POUCH = Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                id("pokeball_pouch"),
                new SimpleCraftingRecipeSerializer<>(PokeballPouchRecipe::new)
        );
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AncientExtensionsConstants.MOD_ID, path);
    }
}
