package nl.streats1.ancientextensions.migration;

import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Migration routes — each leg highlights Regions Unexplored biomes and also accepts
 * every vanilla Overworld biome ({@code minecraft:*}) for catch credit.
 */
public final class MigrationRoutes {

    private static final Map<MigrationSeason, List<MigrationLeg>> ROUTES = new EnumMap<>(MigrationSeason.class);

    static {
        ROUTES.put(MigrationSeason.SPRING, List.of(
                leg(2, 15,
                        "regions_unexplored:flower_fields",
                        "regions_unexplored:clover_plains",
                        "regions_unexplored:temperate_grove"
                ),
                leg(2, 15,
                        "regions_unexplored:marsh",
                        "regions_unexplored:bayou",
                        "regions_unexplored:fen"
                ),
                leg(2, 20,
                        "regions_unexplored:magnolia_woodland",
                        "regions_unexplored:orchard",
                        "regions_unexplored:eucalyptus_forest"
                )
        ));
        ROUTES.put(MigrationSeason.SUMMER, List.of(
                leg(2, 15,
                        "regions_unexplored:tropical_river",
                        "regions_unexplored:grassy_beach",
                        "regions_unexplored:rocky_reef"
                ),
                leg(2, 15,
                        "regions_unexplored:outback",
                        "regions_unexplored:arid_mountains",
                        "regions_unexplored:dry_bushland"
                ),
                leg(2, 20,
                        "regions_unexplored:baobab_savanna",
                        "regions_unexplored:joshua_desert",
                        "regions_unexplored:sparse_rainforest"
                )
        ));
        ROUTES.put(MigrationSeason.AUTUMN, List.of(
                leg(2, 15,
                        "regions_unexplored:autumnal_maple_forest",
                        "regions_unexplored:maple_forest",
                        "regions_unexplored:deciduous_forest"
                ),
                leg(2, 15,
                        "regions_unexplored:prairie",
                        "regions_unexplored:barley_fields",
                        "regions_unexplored:grassland"
                ),
                leg(2, 20,
                        "regions_unexplored:redwoods",
                        "regions_unexplored:boreal_taiga",
                        "regions_unexplored:pine_taiga"
                )
        ));
        ROUTES.put(MigrationSeason.WINTER, List.of(
                leg(2, 15,
                        "regions_unexplored:frozen_tundra",
                        "regions_unexplored:cold_boreal_taiga",
                        "regions_unexplored:icy_heights"
                ),
                leg(2, 15,
                        "regions_unexplored:frozen_pine_taiga",
                        "regions_unexplored:spires",
                        "regions_unexplored:alpine_grove"
                ),
                leg(2, 20,
                        "regions_unexplored:cold_river",
                        "regions_unexplored:chalk_cliffs",
                        "regions_unexplored:gravel_beach"
                )
        ));
    }

    private MigrationRoutes() {
    }

    public static List<MigrationLeg> routeFor(MigrationSeason season) {
        return ROUTES.getOrDefault(season, List.of());
    }

    private static MigrationLeg leg(int catches, int bonusRp, String... biomes) {
        return new MigrationLeg(
                List.of(biomes).stream().map(ResourceLocation::parse).toList(),
                catches,
                bonusRp
        );
    }
}
