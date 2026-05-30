package nl.streats1.ancientextensions.pouch;

import nl.streats1.ancientextensions.registry.ModContent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Pre-built pouch stacks for creative tabs and recipe viewers.
 */
public final class PouchDisplayStacks {

    private PouchDisplayStacks() {
    }

    public static ItemStack tierSample(Item pouchItem, PouchTier tier) {
        ItemStack stack = new ItemStack(pouchItem);
        PouchTierData.writeFromStored(stack, tier, PouchTierData.defaultBallId(tier));
        if (tier != PouchTier.POKE) {
            stack.set(
                    net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                    net.minecraft.network.chat.Component.translatable(
                            "item.ancient_extensions.pokeball_pouch.tier." + tier.getId()
                    )
            );
        }
        return stack;
    }

    public static ItemStack tierSample(PouchTier tier) {
        return tierSample(ModContent.POKEBALL_POUCH, tier);
    }

    public static void acceptAllTiers(java.util.function.Consumer<ItemStack> output, Item pouchItem) {
        for (PouchTier tier : PouchTier.values()) {
            output.accept(tierSample(pouchItem, tier));
        }
    }
}
