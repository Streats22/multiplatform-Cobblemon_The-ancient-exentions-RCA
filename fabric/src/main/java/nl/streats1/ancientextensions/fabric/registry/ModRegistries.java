package nl.streats1.ancientextensions.fabric.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.block.*;
import nl.streats1.ancientextensions.integration.OptionalIntegrationMods;
import nl.streats1.ancientextensions.item.*;
import nl.streats1.ancientextensions.menu.*;
import nl.streats1.ancientextensions.menu.sync.*;
import nl.streats1.ancientextensions.recipe.PokeballPouchRecipe;
import nl.streats1.ancientextensions.registry.ModContent;
import nl.streats1.ancientextensions.registry.ModMenuTypes;
import nl.streats1.ancientextensions.registry.ModRecipeSerializers;

public final class ModRegistries {

    private ModRegistries() {
    }

    public static void register() {
        ModContent.FIELD_SURVEY_CALENDAR_BLOCK = Registry.register(
                BuiltInRegistries.BLOCK,
                id("field_survey_calendar"),
                new FieldSurveyCalendarBlock(
                        BlockBehaviour.Properties.of()
                                .strength(0.6f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                .noOcclusion()
                )
        );

        Registry.register(
                BuiltInRegistries.ITEM,
                id("field_survey_calendar"),
                new SurveyBlockItem(
                        ModContent.FIELD_SURVEY_CALENDAR_BLOCK,
                        new Item.Properties(),
                        "ancient_extensions.guide.role.calendar",
                        "item.ancient_extensions.field_survey_calendar.description",
                        "ancient_extensions.guide.field_calendar_action",
                        "ancient_extensions.guide.field_calendar_detail1",
                        "ancient_extensions.guide.field_calendar_detail2"
                )
        );

        if (OptionalIntegrationMods.hasCreate()) {
            registerCreateFieldKit();
        }

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
                BlockEntityType.Builder.of(
                        PokeballPouchBlockEntity::new,
                        ModContent.POKEBALL_POUCH_BLOCK
                ).build(null)
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
        ModMenuTypes.MIGRATION_ROUTE_CHART = Registry.register(
                BuiltInRegistries.MENU,
                id("migration_route_chart"),
                new ExtendedScreenHandlerType<>(
                        (syncId, inv, data) -> new MigrationRouteChartMenu(syncId, inv, data),
                        ChartOpenData.STREAM_CODEC
                )
        );
        ModMenuTypes.FIELD_SURVEY_TABLET = Registry.register(
                BuiltInRegistries.MENU,
                id("field_survey_tablet"),
                new ExtendedScreenHandlerType<>(
                        (syncId, inv, data) -> new FieldSurveyTabletMenu(syncId, inv, data),
                        TabletOpenData.STREAM_CODEC
                )
        );
        ModMenuTypes.FIELD_SURVEY_CALENDAR = Registry.register(
                BuiltInRegistries.MENU,
                id("field_survey_calendar"),
                new ExtendedScreenHandlerType<>(
                        (syncId, inv, data) -> new FieldSurveyCalendarMenu(syncId, inv, data),
                        ChartOpenData.STREAM_CODEC
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
        ModContent.FIELD_SURVEY_TABLET = Registry.register(
                BuiltInRegistries.ITEM,
                id("field_survey_tablet"),
                new FieldSurveyTabletItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
        );
        ModContent.MIGRATION_ROUTE_CHART = Registry.register(
                BuiltInRegistries.ITEM,
                id("migration_route_chart"),
                new MigrationRouteChartItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON))
        );
        ModContent.MIGRATION_ROUTE_COMPASS = Registry.register(
                BuiltInRegistries.ITEM,
                id("migration_route_compass"),
                new MigrationRouteCompassItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
        );
        ModContent.REGIONAL_PASSPORT = Registry.register(
                BuiltInRegistries.ITEM,
                id("regional_passport"),
                new RegionalPassportItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
        );
        ModContent.SHINY_CHARM = Registry.register(
                BuiltInRegistries.ITEM,
                id("shiny_charm"),
                new ShinyCharmItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant())
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

    private static void registerCreateFieldKit() {
        ModContent.FIELD_SURVEY_SENSOR_BLOCK = Registry.register(
                BuiltInRegistries.BLOCK,
                id("field_survey_sensor"),
                new FieldSurveySensorBlock(
                        BlockBehaviour.Properties.of()
                                .strength(1.2f)
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)
                )
        );

        ModContent.FIELD_SURVEY_MONITOR_BLOCK = Registry.register(
                BuiltInRegistries.BLOCK,
                id("field_survey_monitor"),
                new FieldSurveyMonitorBlock(
                        BlockBehaviour.Properties.of()
                                .strength(1.2f)
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)
                                .noOcclusion()
                )
        );

        ModContent.FIELD_SURVEY_MONITOR_BE = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id("field_survey_monitor"),
                BlockEntityType.Builder.of(
                        FieldSurveyMonitorBlockEntity::new,
                        ModContent.FIELD_SURVEY_MONITOR_BLOCK
                ).build(null)
        );

        Registry.register(
                BuiltInRegistries.ITEM,
                id("field_survey_sensor"),
                new SurveyBlockItem(
                        ModContent.FIELD_SURVEY_SENSOR_BLOCK,
                        new Item.Properties(),
                        "ancient_extensions.guide.role.sensor",
                        "item.ancient_extensions.field_survey_sensor.description",
                        "ancient_extensions.guide.field_sensor_action",
                        "ancient_extensions.guide.field_sensor_detail1",
                        "ancient_extensions.guide.field_sensor_detail2"
                )
        );

        Registry.register(
                BuiltInRegistries.ITEM,
                id("field_survey_monitor"),
                new SurveyBlockItem(
                        ModContent.FIELD_SURVEY_MONITOR_BLOCK,
                        new Item.Properties(),
                        "ancient_extensions.guide.role.monitor",
                        "item.ancient_extensions.field_survey_monitor.description",
                        "ancient_extensions.guide.field_monitor_action",
                        "ancient_extensions.guide.field_monitor_detail1",
                        "ancient_extensions.guide.field_monitor_detail2"
                )
        );
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AncientExtensionsConstants.MOD_ID, path);
    }
}
