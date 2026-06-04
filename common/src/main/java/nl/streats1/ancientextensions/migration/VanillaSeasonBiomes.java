package nl.streats1.ancientextensions.migration;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Fallback route biomes when no Regions Unexplored / Biomes O' Plenty is installed.
 */
public final class VanillaSeasonBiomes {

    private static final String NAMESPACE = "minecraft";

    private VanillaSeasonBiomes() {
    }

    public static List<ResourceLocation> forSeason(MigrationSeason season) {
        return switch (season) {
            case SPRING -> SPRING;
            case SUMMER -> SUMMER;
            case AUTUMN -> AUTUMN;
            case WINTER -> WINTER;
        };
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }

    private static final List<ResourceLocation> SPRING = List.of(
            id("plains"),
            id("sunflower_plains"),
            id("flower_forest"),
            id("birch_forest"),
            id("meadow"),
            id("swamp"),
            id("mangrove_swamp"),
            id("cherry_grove"),
            id("lush_caves"),
            id("dripstone_caves"),
            id("river"),
            id("beach")
    );

    private static final List<ResourceLocation> SUMMER = List.of(
            id("desert"),
            id("badlands"),
            id("eroded_badlands"),
            id("savanna"),
            id("savanna_plateau"),
            id("jungle"),
            id("sparse_jungle"),
            id("bamboo_jungle"),
            id("warm_ocean"),
            id("stony_shore"),
            id("windswept_savanna")
    );

    private static final List<ResourceLocation> AUTUMN = List.of(
            id("forest"),
            id("dark_forest"),
            id("taiga"),
            id("old_growth_pine_taiga"),
            id("old_growth_spruce_taiga"),
            id("windswept_hills"),
            id("windswept_gravelly_hills"),
            id("river"),
            id("stony_peaks"),
            id("meadow"),
            id("plains")
    );

    private static final List<ResourceLocation> WINTER = List.of(
            id("snowy_plains"),
            id("snowy_taiga"),
            id("ice_spikes"),
            id("frozen_peaks"),
            id("jagged_peaks"),
            id("frozen_river"),
            id("grove"),
            id("snowy_slopes"),
            id("cold_ocean"),
            id("deep_frozen_ocean"),
            id("frozen_ocean")
    );
}
