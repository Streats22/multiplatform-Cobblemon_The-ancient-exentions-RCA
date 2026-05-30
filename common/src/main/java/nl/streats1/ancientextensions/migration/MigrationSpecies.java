package nl.streats1.ancientextensions.migration;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Species that count toward migration legs when caught in the correct route biome.
 * <p>
 * Almost every Flying-type Pokémon counts (primary or secondary type). A small set of
 * non-flying seasonal survey species also counts and is listed on the migration chart.
 * Migratory spawn pools are generated with {@code scripts/generate_migration_spawns.py}.
 */
public final class MigrationSpecies {

    /** Non-flying species that still count on their season's route (also have dedicated spawns). */
    private static final Map<MigrationSeason, Set<ResourceLocation>> SEASONAL_SURVEY = new EnumMap<>(MigrationSeason.class);

    static {
        SEASONAL_SURVEY.put(MigrationSeason.SPRING, Set.of(
                id("cobblemon:burmy"),
                id("cobblemon:budew"),
                id("cobblemon:oddish"),
                id("cobblemon:scatterbug"),
                id("cobblemon:flabebe"),
                id("cobblemon:wooper")
        ));
        SEASONAL_SURVEY.put(MigrationSeason.SUMMER, Set.of(
                id("cobblemon:magikarp"),
                id("cobblemon:remoraid"),
                id("cobblemon:corphish"),
                id("cobblemon:lotad"),
                id("cobblemon:surskit"),
                id("cobblemon:krabby"),
                id("cobblemon:mudkip"),
                id("cobblemon:arrokuda")
        ));
        SEASONAL_SURVEY.put(MigrationSeason.AUTUMN, Set.of(
                id("cobblemon:seedot"),
                id("cobblemon:nuzleaf"),
                id("cobblemon:shroomish"),
                id("cobblemon:patrat"),
                id("cobblemon:pumpkaboo"),
                id("cobblemon:deerling"),
                id("cobblemon:pineco"),
                id("cobblemon:bouffalant"),
                id("cobblemon:foongus")
        ));
        SEASONAL_SURVEY.put(MigrationSeason.WINTER, Set.of(
                id("cobblemon:snover"),
                id("cobblemon:snom"),
                id("cobblemon:cubchoo"),
                id("cobblemon:swinub"),
                id("cobblemon:spheal"),
                id("cobblemon:bergmite"),
                id("cobblemon:snorunt"),
                id("cobblemon:cetoddle")
        ));
    }

    private MigrationSpecies() {
    }

    public static boolean isMigratory(MigrationSeason season, ResourceLocation speciesId) {
        if (hasFlyingType(speciesId)) {
            return true;
        }
        Set<ResourceLocation> survey = SEASONAL_SURVEY.get(season);
        return survey != null && survey.contains(speciesId);
    }

    /** Featured non-flying survey species shown on the migration chart (flying types are summarized in lang). */
    public static Set<ResourceLocation> speciesForSeason(MigrationSeason season) {
        return SEASONAL_SURVEY.getOrDefault(season, Set.of());
    }

    public static boolean hasFlyingType(ResourceLocation speciesId) {
        Species species = PokemonSpecies.INSTANCE.getByIdentifier(speciesId);
        if (species == null) {
            return false;
        }
        return isFlying(species.getPrimaryType()) || isFlying(species.getSecondaryType());
    }

    private static boolean isFlying(ElementalType type) {
        return type != null && type == ElementalTypes.FLYING;
    }

    private static ResourceLocation id(String raw) {
        return ResourceLocation.parse(raw);
    }
}
