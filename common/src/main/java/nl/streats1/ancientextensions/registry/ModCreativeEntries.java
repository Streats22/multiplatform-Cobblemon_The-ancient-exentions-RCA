package nl.streats1.ancientextensions.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import nl.streats1.ancientextensions.pouch.PouchDisplayStacks;

/**
 * All stacks shown in the Ancient Extensions creative tab.
 */
public final class ModCreativeEntries {

    private ModCreativeEntries() {
    }

    public static void acceptAll(java.util.function.Consumer<ItemStack> output) {
        acceptItem(output, ModContent.ANCIENT_PROFESSORS_KIT);
        acceptItem(output, ModContent.REGIONAL_SURVEY_JOURNAL);
        acceptItem(output, ModContent.FIELD_SURVEY_TABLET);
        acceptItem(output, ModContent.MIGRATION_ROUTE_CHART);
        acceptItem(output, ModContent.MIGRATION_ROUTE_COMPASS);
        acceptItem(output, ModContent.REGIONAL_PASSPORT);
        acceptItem(output, ModContent.SHINY_CHARM);
        acceptPouchVariants(output, ModContent.POKEBALL_POUCH);
        acceptBlock(output, ModContent.POKEBALL_POUCH_BLOCK);
        acceptBlock(output, ModContent.FIELD_SURVEY_SENSOR_BLOCK);
        acceptBlock(output, ModContent.FIELD_SURVEY_MONITOR_BLOCK);
        acceptBlock(output, ModContent.FIELD_SURVEY_CALENDAR_BLOCK);
        acceptItem(output, ModContent.FIELD_SURVEY_TELEMETRY_UPGRADE);
    }

    public static void acceptPouchVariants(java.util.function.Consumer<ItemStack> output, Item pouchItem) {
        if (pouchItem == null) {
            return;
        }
        PouchDisplayStacks.acceptAllTiers(output, pouchItem);
    }

    private static void acceptItem(java.util.function.Consumer<ItemStack> output, Item item) {
        if (item == null) {
            return;
        }
        output.accept(new ItemStack(item));
    }

    private static void acceptBlock(java.util.function.Consumer<ItemStack> output, Block block) {
        if (block == null) {
            return;
        }
        ItemStack stack = new ItemStack(block);
        if (!stack.isEmpty()) {
            output.accept(stack);
        }
    }
}
