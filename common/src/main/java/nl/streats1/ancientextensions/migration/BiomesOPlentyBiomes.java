package nl.streats1.ancientextensions.migration;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Biomes O' Plenty overworld catalog (1.21.x registry names).
 * Used only when {@link nl.streats1.ancientextensions.integration.OptionalIntegrationMods#hasBiomesOPlenty()} is true.
 */
public final class BiomesOPlentyBiomes {

    public static final String NAMESPACE = "biomesoplenty";

    private BiomesOPlentyBiomes() {
    }

    public static List<ResourceLocation> forSeason(MigrationSeason season) {
        return switch (season) {
            case SPRING -> SPRING;
            case SUMMER -> SUMMER;
            case AUTUMN -> AUTUMN;
            case WINTER -> WINTER;
        };
    }

    public static boolean isBiomesOPlenty(ResourceLocation biomeId) {
        return biomeId != null && NAMESPACE.equals(biomeId.getNamespace());
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }

    /**
     * Lush, wetland, and magical temperate biomes.
     */
    private static final List<ResourceLocation> SPRING = List.of(
            id("bayou"),
            id("bog"),
            id("marsh"),
            id("wetland"),
            id("floodplain"),
            id("moor"),
            id("mystic_grove"),
            id("lavender_field"),
            id("jacaranda_glade"),
            id("orchard"),
            id("overgrown_greens"),
            id("fungal_jungle"),
            id("pumpkin_patch"),
            id("pasture"),
            id("field"),
            id("forested_field"),
            id("prairie"),
            id("hot_springs"),
            id("glowing_grotto")
    );

    /**
     * Hot, arid, and tropical overworld biomes.
     */
    private static final List<ResourceLocation> SUMMER = List.of(
            id("tropics"),
            id("rainforest"),
            id("rocky_rainforest"),
            id("lush_savanna"),
            id("lush_desert"),
            id("dryland"),
            id("scrubland"),
            id("shrubland"),
            id("rocky_shrubland"),
            id("volcanic_plains"),
            id("volcano"),
            id("dune_beach"),
            id("gravel_beach"),
            id("jade_cliffs"),
            id("mediterranean_forest"),
            id("wasteland"),
            id("wasteland_steppe"),
            id("origin_valley")
    );

    /**
     * Deciduous, highland, and harvest-season temperate biomes.
     */
    private static final List<ResourceLocation> AUTUMN = List.of(
            id("seasonal_forest"),
            id("maple_woods"),
            id("woodland"),
            id("old_growth_woodland"),
            id("dead_forest"),
            id("old_growth_dead_forest"),
            id("grassland"),
            id("highland"),
            id("aspen_glade"),
            id("coniferous_forest"),
            id("fir_clearing"),
            id("redwood_forest"),
            id("ominous_woods"),
            id("crag"),
            id("spider_nest")
    );

    /**
     * Snowy overworld plus nether biomes for the winter front.
     */
    private static final List<ResourceLocation> WINTER = List.of(
            id("muskeg"),
            id("tundra"),
            id("auroral_garden"),
            id("snowy_coniferous_forest"),
            id("snowy_maple_woods"),
            id("snowy_fir_clearing"),
            id("snowblossom_grove"),
            id("cold_desert"),
            id("wintry_origin_valley"),
            id("crystalline_chasm"),
            id("erupting_inferno"),
            id("undergrowth"),
            id("visceral_heap"),
            id("withered_abyss")
    );
}
