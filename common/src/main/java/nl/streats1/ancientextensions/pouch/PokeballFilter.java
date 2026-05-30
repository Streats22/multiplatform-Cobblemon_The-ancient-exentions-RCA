package nl.streats1.ancientextensions.pouch;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Accepts every ball in Cobblemon's {@code cobblemon:poke_balls} tag (apricorn, ancient, etc.).
 */
public final class PokeballFilter {

    public static final TagKey<Item> COBBLEMON_POKE_BALLS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("cobblemon", "poke_balls")
    );

    private PokeballFilter() {
    }

    public static boolean isPokeball(ItemStack stack) {
        return !stack.isEmpty() && stack.is(COBBLEMON_POKE_BALLS);
    }
}
