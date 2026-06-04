package nl.streats1.ancientextensions.registry;

import nl.streats1.ancientextensions.block.PokeballPouchBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Populated by each platform loader when registries are bound.
 */
public final class ModContent {

    public static Item MIGRATION_ROUTE_CHART;
    public static Item ANCIENT_PROFESSORS_KIT;
    public static Item REGIONAL_SURVEY_JOURNAL;
    public static Item FIELD_SURVEY_TABLET;
    public static Item REGIONAL_PASSPORT;
    public static Item POKEBALL_POUCH;
    public static Block POKEBALL_POUCH_BLOCK;
    public static BlockEntityType<PokeballPouchBlockEntity> POKEBALL_POUCH_BE;

    private ModContent() {
    }
}
