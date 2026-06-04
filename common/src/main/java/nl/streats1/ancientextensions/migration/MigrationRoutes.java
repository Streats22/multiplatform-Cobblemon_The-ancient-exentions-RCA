package nl.streats1.ancientextensions.migration;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Migration routes — each leg partitions biomes from optional world-gen mods (RU, BOP) when present,
 * otherwise {@link VanillaSeasonBiomes}. Any vanilla Overworld biome ({@code minecraft:*}) always
 * counts for catch credit (including Regions Unexplored: Expansion remodels).
 */
public final class MigrationRoutes {

    private static Map<MigrationSeason, List<MigrationLeg>> routes;

    private static Map<MigrationSeason, List<MigrationLeg>> routes() {
        if (routes == null) {
            Map<MigrationSeason, List<MigrationLeg>> built = new EnumMap<>(MigrationSeason.class);
            for (MigrationSeason season : MigrationSeason.values()) {
                built.put(season, buildSeasonRoute(season));
            }
            routes = Map.copyOf(built);
        }
        return routes;
    }

    private MigrationRoutes() {
    }

    public static List<MigrationLeg> routeFor(MigrationSeason season) {
        return routes().getOrDefault(season, List.of());
    }

    /**
     * All biome IDs featured across every seasonal route (for spawn pools and validation).
     */
    public static List<ResourceLocation> allFeaturedBiomes() {
        List<ResourceLocation> biomes = new ArrayList<>();
        for (MigrationSeason season : MigrationSeason.values()) {
            for (MigrationLeg leg : routeFor(season)) {
                biomes.addAll(leg.biomeIds());
            }
        }
        return List.copyOf(biomes);
    }

    /**
     * Biome IDs for one season (union of its three legs).
     */
    public static List<ResourceLocation> biomesForSeason(MigrationSeason season) {
        return MigrationBiomeCatalog.biomesForSeason(season);
    }

    private static List<MigrationLeg> buildSeasonRoute(MigrationSeason season) {
        List<List<ResourceLocation>> partitions = RegionsUnexploredBiomes.partitionForLegs(
                MigrationBiomeCatalog.biomesForSeason(season)
        );
        List<MigrationLeg> legs = new ArrayList<>(3);
        legs.add(leg(2, 15, partitions.get(0)));
        legs.add(leg(2, 15, partitions.get(1)));
        legs.add(leg(2, 20, partitions.get(2)));
        return List.copyOf(legs);
    }

    private static MigrationLeg leg(int catches, int bonusRp, List<ResourceLocation> biomes) {
        return new MigrationLeg(List.copyOf(biomes), catches, bonusRp);
    }
}
