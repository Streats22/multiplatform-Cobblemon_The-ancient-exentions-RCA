package nl.streats1.ancientextensions.registry;

import nl.streats1.ancientextensions.pouch.PouchDisplayStacks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * All stacks shown in the Ancient Extensions creative tab.
 */
public final class ModCreativeEntries {

    private ModCreativeEntries() {
    }

    public static void acceptAll(java.util.function.Consumer<ItemStack> output) {
        output.accept(new ItemStack(ModContent.ANCIENT_PROFESSORS_KIT));
        output.accept(new ItemStack(ModContent.REGIONAL_SURVEY_JOURNAL));
        output.accept(new ItemStack(ModContent.MIGRATION_ROUTE_CHART));
        output.accept(new ItemStack(ModContent.REGIONAL_PASSPORT));
        acceptPouchVariants(output, ModContent.POKEBALL_POUCH);
        if (ModContent.POKEBALL_POUCH_BLOCK != null) {
            output.accept(new ItemStack(ModContent.POKEBALL_POUCH_BLOCK));
        }
    }

    public static void acceptPouchVariants(java.util.function.Consumer<ItemStack> output, Item pouchItem) {
        if (pouchItem == null) {
            return;
        }
        PouchDisplayStacks.acceptAllTiers(output, pouchItem);
    }
}
