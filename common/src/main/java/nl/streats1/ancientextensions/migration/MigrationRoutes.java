package nl.streats1.ancientextensions.migration;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Migration routes — each leg highlights a partition of Regions Unexplored biomes for the season
 * and also accepts every vanilla Overworld biome ({@code minecraft:*}) for catch credit
 * (including Regions Unexplored: Expansion remodels).
 */
public final class MigrationRoutes {

    private static final Map<MigrationSeason, List<MigrationLeg>> ROUTES = new EnumMap<>(MigrationSeason.class);

    static {
        for (MigrationSeason season : MigrationSeason.values()) {
            ROUTES.put(season, buildSeasonRoute(season));
        }
    }

    private MigrationRoutes() {
    }

    public static List<MigrationLeg> routeFor(MigrationSeason season) {
        return ROUTES.getOrDefault(season, List.of());
    }

    /** All biome IDs featured across every seasonal route (for spawn pools and validation). */
    public static List<ResourceLocation> allFeaturedBiomes() {
        List<ResourceLocation> biomes = new ArrayList<>();
        for (MigrationSeason season : MigrationSeason.values()) {
            for (MigrationLeg leg : routeFor(season)) {
                biomes.addAll(leg.biomeIds());
            }
        }
        return List.copyOf(biomes);
    }

    /** Biome IDs for one season (union of its three legs). */
    public static List<ResourceLocation> biomesForSeason(MigrationSeason season) {
        return RegionsUnexploredBiomes.forSeason(season);
    }

    private static List<MigrationLeg> buildSeasonRoute(MigrationSeason season) {
        List<List<ResourceLocation>> partitions = RegionsUnexploredBiomes.partitionForLegs(
                RegionsUnexploredBiomes.forSeason(season)
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
