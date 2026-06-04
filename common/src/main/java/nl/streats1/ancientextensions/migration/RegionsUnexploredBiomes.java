package nl.streats1.ancientextensions.migration;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Complete Regions Unexplored biome catalog (v0.5.x, 71 biomes).
 * Only merged into routes when the mod is loaded — see {@link MigrationBiomeCatalog}.
 * {@link RegionsUnexploredExpansion} vanilla remodels are covered via {@code minecraft:*} in {@link MigrationLeg}.
 */
public final class RegionsUnexploredBiomes {

    public static final String NAMESPACE = "regions_unexplored";

    private static final List<ResourceLocation> SPRING = buildSpring();
    private static final List<ResourceLocation> SUMMER = buildSummer();
    private static final List<ResourceLocation> AUTUMN = buildAutumn();
    private static final List<ResourceLocation> WINTER = buildWinter();
    private static final List<ResourceLocation> ALL = buildAll();

    private RegionsUnexploredBiomes() {
    }

    public static List<ResourceLocation> all() {
        return ALL;
    }

    public static List<ResourceLocation> forSeason(MigrationSeason season) {
        return switch (season) {
            case SPRING -> SPRING;
            case SUMMER -> SUMMER;
            case AUTUMN -> AUTUMN;
            case WINTER -> WINTER;
        };
    }

    public static boolean isRegionsUnexplored(ResourceLocation biomeId) {
        return biomeId != null && NAMESPACE.equals(biomeId.getNamespace());
    }

    /** Splits a season's biomes evenly across three migration legs. */
    public static List<List<ResourceLocation>> partitionForLegs(List<ResourceLocation> biomes) {
        if (biomes.isEmpty()) {
            return List.of(List.of(), List.of(), List.of());
        }
        int chunk = (biomes.size() + 2) / 3;
        List<List<ResourceLocation>> legs = new ArrayList<>(3);
        for (int leg = 0; leg < 3; leg++) {
            int start = leg * chunk;
            if (start >= biomes.size()) {
                legs.add(List.of());
            } else {
                legs.add(List.copyOf(biomes.subList(start, Math.min(start + chunk, biomes.size()))));
            }
        }
        return legs;
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }

    private static List<ResourceLocation> buildAll() {
        List<ResourceLocation> biomes = new ArrayList<>();
        biomes.addAll(buildSpring());
        biomes.addAll(buildSummer());
        biomes.addAll(buildAutumn());
        biomes.addAll(buildWinter());
        return Collections.unmodifiableList(biomes);
    }

    /** Lush / wet overworld biomes plus two cave biomes. */
    private static List<ResourceLocation> buildSpring() {
        return List.of(
                id("flower_fields"),
                id("clover_plains"),
                id("temperate_grove"),
                id("marsh"),
                id("bayou"),
                id("fen"),
                id("magnolia_woodland"),
                id("orchard"),
                id("eucalyptus_forest"),
                id("bamboo_forest"),
                id("alpha_grove"),
                id("fungal_fen"),
                id("hyacinth_deeps"),
                id("muddy_river"),
                id("old_growth_bayou"),
                id("willow_forest"),
                id("poppy_fields"),
                id("pumpkin_fields"),
                id("ancient_delta"),
                id("bioshroom_caves")
        );
    }

    /** Hot / tropical overworld biomes plus one cave biome. */
    private static List<ResourceLocation> buildSummer() {
        return List.of(
                id("tropical_river"),
                id("grassy_beach"),
                id("rocky_reef"),
                id("outback"),
                id("arid_mountains"),
                id("dry_bushland"),
                id("baobab_savanna"),
                id("joshua_desert"),
                id("sparse_rainforest"),
                id("tropics"),
                id("rainforest"),
                id("saguaro_desert"),
                id("shrubland"),
                id("steppe"),
                id("rocky_meadow"),
                id("highland_fields"),
                id("mountains"),
                id("gravel_beach"),
                id("prismachasm")
        );
    }

    /** Deciduous / harvest overworld biomes plus one cave biome. */
    private static List<ResourceLocation> buildAutumn() {
        return List.of(
                id("autumnal_maple_forest"),
                id("maple_forest"),
                id("deciduous_forest"),
                id("cold_deciduous_forest"),
                id("barley_fields"),
                id("grassland"),
                id("prairie"),
                id("redwoods"),
                id("sparse_redwoods"),
                id("boreal_taiga"),
                id("silver_birch_forest"),
                id("ashen_woodland"),
                id("towering_cliffs"),
                id("mauve_hills"),
                id("redstone_caves")
        );
    }

    /** Cold overworld, remaining caves, and all Nether biomes. */
    private static List<ResourceLocation> buildWinter() {
        return List.of(
                id("frozen_tundra"),
                id("cold_boreal_taiga"),
                id("icy_heights"),
                id("frozen_pine_taiga"),
                id("spires"),
                id("cold_river"),
                id("blackwood_taiga"),
                id("pine_taiga"),
                id("pine_slopes"),
                id("golden_boreal_taiga"),
                id("chalk_cliffs"),
                id("scorching_caves"),
                id("blackstone_basin"),
                id("glistering_meadow"),
                id("infernal_holt"),
                id("mycotoxic_undergrowth"),
                id("redstone_abyss")
        );
    }
}
