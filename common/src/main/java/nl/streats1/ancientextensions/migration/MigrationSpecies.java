package nl.streats1.ancientextensions.migration;

import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Species that count toward migration legs when caught in the correct route biome.
 * Tune per pack; must align with migratory spawn pool datapacks.
 */
public final class MigrationSpecies {

    private static final Map<MigrationSeason, Set<ResourceLocation>> BY_SEASON = new EnumMap<>(MigrationSeason.class);

    static {
        BY_SEASON.put(MigrationSeason.SPRING, Set.of(
                id("cobblemon:hoppip"),
                id("cobblemon:skiploom"),
                id("cobblemon:burmy"),
                id("cobblemon:combee"),
                id("cobblemon:budew")
        ));
        BY_SEASON.put(MigrationSeason.SUMMER, Set.of(
                id("cobblemon:wingull"),
                id("cobblemon:pelipper"),
                id("cobblemon:magikarp"),
                id("cobblemon:remoraid"),
                id("cobblemon:corphish")
        ));
        BY_SEASON.put(MigrationSeason.AUTUMN, Set.of(
                id("cobblemon:seedot"),
                id("cobblemon:nuzleaf"),
                id("cobblemon:shroomish"),
                id("cobblemon:patrat"),
                id("cobblemon:pumpkaboo")
        ));
        BY_SEASON.put(MigrationSeason.WINTER, Set.of(
                id("cobblemon:snover"),
                id("cobblemon:snom"),
                id("cobblemon:cubchoo"),
                id("cobblemon:swinub"),
                id("cobblemon:spheal")
        ));
    }

    private MigrationSpecies() {
    }

    public static boolean isMigratory(MigrationSeason season, ResourceLocation speciesId) {
        Set<ResourceLocation> pool = BY_SEASON.get(season);
        return pool != null && pool.contains(speciesId);
    }

    private static ResourceLocation id(String raw) {
        return ResourceLocation.parse(raw);
    }
}
