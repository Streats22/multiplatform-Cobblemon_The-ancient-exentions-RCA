package nl.streats1.ancientextensions.kit;

import nl.streats1.ancientextensions.pouch.PokeballFilter;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Starter supplies for the Ancient Professor's Field Kit (Rubius / Cobblemon survey tuning).
 */
public final class ProfessorsKitRewards {

    public static final int POKE_BALL_TOTAL = 15;
    public static final int POTION_COUNT = 6;
    public static final int REVIVE_COUNT = 2;

    private static final ResourceLocation MASTER_BALL = ResourceLocation.fromNamespaceAndPath("cobblemon", "master_ball");

    private ProfessorsKitRewards() {
    }

    /** Items given directly to the player when the kit is opened (inventory mode). */
    public static List<ItemStack> createPlayerStacks(HolderLookup.Provider registries, RandomSource random) {
        return createStarterSupplies(registries, random);
    }

    /** Starter supplies stored in the camp chest. */
    public static List<ItemStack> createDeployChestStacks(
            HolderLookup.Provider registries,
            RandomSource random,
            boolean chestOnlyStarterSupplies
    ) {
        if (chestOnlyStarterSupplies) {
            return createStarterSupplies(registries, random);
        }
        return List.of();
    }

    private static List<ItemStack> createStarterSupplies(HolderLookup.Provider registries, RandomSource random) {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.addAll(randomPokeBallStacks(registries, random, POKE_BALL_TOTAL));
        stacks.add(stack("cobblemon:potion", POTION_COUNT));
        stacks.add(stack("cobblemon:revive", REVIVE_COUNT));
        return stacks;
    }

    /**
     * Picks random balls from {@code #cobblemon:poke_balls} (apricorn, ancient, etc.).
     * Master Balls are excluded from the starter pool.
     */
    private static List<ItemStack> randomPokeBallStacks(
            HolderLookup.Provider registries,
            RandomSource random,
            int total
    ) {
        List<Item> pool = registries.lookupOrThrow(Registries.ITEM)
                .getOrThrow(PokeballFilter.COBBLEMON_POKE_BALLS)
                .stream()
                .map(Holder::value)
                .filter(ProfessorsKitRewards::isStarterBall)
                .toList();

        if (pool.isEmpty()) {
            return List.of(stack("cobblemon:poke_ball", total));
        }

        Map<Item, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < total; i++) {
            Item ball = pool.get(random.nextInt(pool.size()));
            counts.merge(ball, 1, Integer::sum);
        }

        List<ItemStack> stacks = new ArrayList<>(counts.size());
        for (Map.Entry<Item, Integer> entry : counts.entrySet()) {
            stacks.add(new ItemStack(entry.getKey(), entry.getValue()));
        }
        return stacks;
    }

    private static boolean isStarterBall(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return id != null && !MASTER_BALL.equals(id);
    }

    private static ItemStack stack(String itemId, int count) {
        ResourceLocation id = ResourceLocation.parse(itemId);
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, count);
    }
}
