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
import java.util.Set;

/**
 * Starter supplies for the Ancient Professor's Field Kit (Rubius / Cobblemon survey tuning).
 */
public final class ProfessorsKitRewards {

    public static final int POKE_BALL_MIN = 8;
    public static final int POKE_BALL_MAX = 12;
    public static final int POTION_COUNT = 6;
    public static final int REVIVE_COUNT = 2;

    /** Maximum ball tier for camp chest loot — Ultra and below (no Beast / Master). */
    private static final int MAX_BALL_TIER = 3;

    private static final Set<ResourceLocation> DENIED_BALLS = Set.of(
            id("cobblemon:master_ball"),
            id("cobblemon:beast_ball")
    );

    private static final List<ResourceLocation> STANDARD_BALL_IDS = List.of(
            id("cobblemon:poke_ball"),
            id("cobblemon:great_ball"),
            id("cobblemon:ultra_ball")
    );

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
        int ballCount = POKE_BALL_MIN + random.nextInt(POKE_BALL_MAX - POKE_BALL_MIN + 1);
        stacks.addAll(randomPokeBallStacks(registries, random, ballCount));
        stacks.add(stack("cobblemon:potion", POTION_COUNT));
        stacks.add(stack("cobblemon:revive", REVIVE_COUNT));
        return stacks;
    }

    /**
     * Random mix of standard apricorn balls (Poké / Great / Ultra) and ancient balls up to ancient Ultra tier.
     */
    private static List<ItemStack> randomPokeBallStacks(
            HolderLookup.Provider registries,
            RandomSource random,
            int total
    ) {
        List<Item> standardPool = resolveStandardBalls();
        List<Item> ancientPool = resolveAncientBalls(registries);

        if (standardPool.isEmpty() && ancientPool.isEmpty()) {
            return List.of(stack("cobblemon:poke_ball", total));
        }

        Map<Item, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < total; i++) {
            Item ball = pickBall(random, standardPool, ancientPool);
            counts.merge(ball, 1, Integer::sum);
        }

        List<ItemStack> stacks = new ArrayList<>(counts.size());
        for (Map.Entry<Item, Integer> entry : counts.entrySet()) {
            stacks.add(new ItemStack(entry.getKey(), entry.getValue()));
        }
        return stacks;
    }

    private static Item pickBall(RandomSource random, List<Item> standardPool, List<Item> ancientPool) {
        boolean wantAncient = !ancientPool.isEmpty() && (standardPool.isEmpty() || random.nextBoolean());
        if (wantAncient) {
            return ancientPool.get(random.nextInt(ancientPool.size()));
        }
        if (!standardPool.isEmpty()) {
            return standardPool.get(random.nextInt(standardPool.size()));
        }
        return ancientPool.get(random.nextInt(ancientPool.size()));
    }

    private static List<Item> resolveStandardBalls() {
        List<Item> items = new ArrayList<>();
        for (ResourceLocation ballId : STANDARD_BALL_IDS) {
            Item item = BuiltInRegistries.ITEM.get(ballId);
            if (item != Items.AIR) {
                items.add(item);
            }
        }
        return List.copyOf(items);
    }

    private static List<Item> resolveAncientBalls(HolderLookup.Provider registries) {
        return registries.lookupOrThrow(Registries.ITEM)
                .getOrThrow(PokeballFilter.COBBLEMON_POKE_BALLS)
                .stream()
                .map(Holder::value)
                .filter(ProfessorsKitRewards::isAncientStarterBall)
                .toList();
    }

    private static boolean isAncientStarterBall(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null || !isAllowedStarterBall(id)) {
            return false;
        }
        return id.getPath().startsWith("ancient");
    }

    private static boolean isAllowedStarterBall(ResourceLocation id) {
        if (!"cobblemon".equals(id.getNamespace())) {
            return false;
        }
        if (DENIED_BALLS.contains(id)) {
            return false;
        }
        String path = id.getPath();
        if (path.contains("master") || path.contains("beast")) {
            return false;
        }
        return ballTierRank(path) <= MAX_BALL_TIER;
    }

    /**
     * Rough tier from item path — 1 = Poké-like, 2 = Great, 3 = Ultra, 4+ = excluded elsewhere.
     */
    private static int ballTierRank(String path) {
        if (path.contains("ultra")) {
            return 3;
        }
        if (path.contains("great")) {
            return 2;
        }
        return 1;
    }

    private static ResourceLocation id(String raw) {
        return ResourceLocation.parse(raw);
    }

    private static ItemStack stack(String itemId, int count) {
        Item item = BuiltInRegistries.ITEM.get(id(itemId));
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, count);
    }
}
