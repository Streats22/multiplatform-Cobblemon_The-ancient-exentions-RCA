package nl.streats1.ancientextensions.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import nl.streats1.ancientextensions.block.FieldSurveyMonitorBlockEntity;
import nl.streats1.ancientextensions.block.PokeballPouchBlockEntity;

/**
 * Populated by each platform loader when registries are bound.
 */
public final class ModContent {

    public static Item MIGRATION_ROUTE_CHART;
    public static Item ANCIENT_PROFESSORS_KIT;
    public static Item REGIONAL_SURVEY_JOURNAL;
    public static Item FIELD_SURVEY_TABLET;
    public static Item MIGRATION_ROUTE_COMPASS;
    public static Item REGIONAL_PASSPORT;
    public static Item POKEBALL_POUCH;
    public static Block POKEBALL_POUCH_BLOCK;
    public static BlockEntityType<PokeballPouchBlockEntity> POKEBALL_POUCH_BE;
    public static Block FIELD_SURVEY_SENSOR_BLOCK;
    public static Block FIELD_SURVEY_MONITOR_BLOCK;
    public static BlockEntityType<FieldSurveyMonitorBlockEntity> FIELD_SURVEY_MONITOR_BE;
    public static Block FIELD_SURVEY_CALENDAR_BLOCK;
    public static Item FIELD_SURVEY_TELEMETRY_UPGRADE;
    public static Item SHINY_CHARM;

    private ModContent() {
    }
}
