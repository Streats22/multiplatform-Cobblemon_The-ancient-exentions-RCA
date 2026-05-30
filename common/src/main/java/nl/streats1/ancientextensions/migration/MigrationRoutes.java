package nl.streats1.ancientextensions.migration;

import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Migration routes for Rubius Cobblemon — each leg accepts Terralith or Regions Unexplored (or vanilla) biomes.
 */
public final class MigrationRoutes {

    private static final Map<MigrationSeason, List<MigrationLeg>> ROUTES = new EnumMap<>(MigrationSeason.class);

    static {
        ROUTES.put(MigrationSeason.SPRING, List.of(
                leg(2, 15,
                        "terralith:blooming_valley",
                        "regions_unexplored:flower_fields",
                        "regions_unexplored:clover_plains"
                ),
                leg(2, 15,
                        "terralith:orchid_swamp",
                        "regions_unexplored:marsh",
                        "regions_unexplored:bayou"
                ),
                leg(2, 20,
                        "terralith:lavender_forest",
                        "regions_unexplored:magnolia_woodland",
                        "regions_unexplored:temperate_grove"
                )
        ));
        ROUTES.put(MigrationSeason.SUMMER, List.of(
                leg(2, 15,
                        "minecraft:warm_ocean",
                        "regions_unexplored:tropical_river",
                        "regions_unexplored:grassy_beach"
                ),
                leg(2, 15,
                        "terralith:arid_highlands",
                        "regions_unexplored:outback",
                        "regions_unexplored:arid_mountains"
                ),
                leg(2, 20,
                        "terralith:red_oasis",
                        "regions_unexplored:baobab_savanna",
                        "regions_unexplored:joshua_desert"
                )
        ));
        ROUTES.put(MigrationSeason.AUTUMN, List.of(
                leg(2, 15,
                        "terralith:maple_forest",
                        "regions_unexplored:autumnal_maple_forest",
                        "regions_unexplored:maple_forest"
                ),
                leg(2, 15,
                        "minecraft:sunflower_plains",
                        "regions_unexplored:prairie",
                        "regions_unexplored:barley_fields"
                ),
                leg(2, 20,
                        "terralith:cloud_forest",
                        "regions_unexplored:redwoods",
                        "regions_unexplored:deciduous_forest"
                )
        ));
        ROUTES.put(MigrationSeason.WINTER, List.of(
                leg(2, 15,
                        "terralith:snowy_shield",
                        "regions_unexplored:frozen_tundra",
                        "regions_unexplored:cold_boreal_taiga"
                ),
                leg(2, 15,
                        "minecraft:ice_spikes",
                        "regions_unexplored:icy_heights",
                        "regions_unexplored:frozen_pine_taiga"
                ),
                leg(2, 20,
                        "terralith:frozen_cliffs",
                        "regions_unexplored:cold_river",
                        "regions_unexplored:chalk_cliffs"
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
