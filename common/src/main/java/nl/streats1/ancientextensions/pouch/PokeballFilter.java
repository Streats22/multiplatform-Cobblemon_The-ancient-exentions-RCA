package nl.streats1.ancientextensions.pouch;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

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

    /** Concrete ball stacks for recipe books and JEI when tag lookup is empty on the client. */
    public static List<ItemStack> sampleStacks() {
        List<ItemStack> samples = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack stack = new ItemStack(item);
            if (!stack.isEmpty() && stack.is(COBBLEMON_POKE_BALLS)) {
                samples.add(stack);
            }
        }
        if (samples.isEmpty()) {
            BuiltInRegistries.ITEM.getOptional(defaultBallId()).ifPresent(item -> samples.add(new ItemStack(item)));
        }
        if (samples.isEmpty()) {
            samples.add(new ItemStack(Items.SNOWBALL));
        }
        return samples;
    }

    public static ResourceLocation defaultBallId() {
        return ResourceLocation.fromNamespaceAndPath("cobblemon", "poke_ball");
    }
}
