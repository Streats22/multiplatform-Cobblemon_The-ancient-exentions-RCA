package nl.streats1.ancientextensions.migration;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Species that count toward migration legs when caught in the correct route biome.
 * <p>
 * Almost every Flying-type Pokémon counts (primary or secondary type). A small set of
 * non-flying seasonal survey species also counts and is listed on the migration chart.
 * Migratory spawn pools are generated with {@code scripts/generate_migration_spawns.py}.
 */
public final class MigrationSpecies {

    /**
     * Non-flying species that still count on their season's route (also have dedicated spawns).
     */
    private static final Map<MigrationSeason, Set<ResourceLocation>> SEASONAL_SURVEY = new EnumMap<>(MigrationSeason.class);
    private static final Map<MigrationSeason, List<ResourceLocation>> ROUTE_SPECIES = loadRouteSpecies();

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

    /**
     * Featured non-flying survey species (also listed in {@link #speciesOnRouteForSeason}).
     */
    public static Set<ResourceLocation> speciesForSeason(MigrationSeason season) {
        return SEASONAL_SURVEY.getOrDefault(season, Set.of());
    }

    /**
     * Every species in the seasonal migration spawn pool (flying route + survey spawns).
     */
    public static List<ResourceLocation> speciesOnRouteForSeason(MigrationSeason season) {
        return ROUTE_SPECIES.getOrDefault(season, List.of());
    }

    private static Map<MigrationSeason, List<ResourceLocation>> loadRouteSpecies() {
        Map<MigrationSeason, List<ResourceLocation>> loaded = new EnumMap<>(MigrationSeason.class);
        for (MigrationSeason season : MigrationSeason.values()) {
            loaded.put(season, loadSpawnPoolSpecies(season));
        }
        return Map.copyOf(loaded);
    }

    private static List<ResourceLocation> loadSpawnPoolSpecies(MigrationSeason season) {
        String path = "/data/cobblemon/spawn_pool_world/ancient_extensions_migration_" + season.getId() + ".json";
        try (InputStream stream = MigrationSpecies.class.getResourceAsStream(path)) {
            if (stream == null) {
                return List.of();
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            JsonArray spawns = root.getAsJsonArray("spawns");
            Set<ResourceLocation> unique = new LinkedHashSet<>();
            for (JsonElement entry : spawns) {
                String pokemon = entry.getAsJsonObject().get("pokemon").getAsString();
                unique.add(ResourceLocation.fromNamespaceAndPath("cobblemon", pokemon));
            }
            return List.copyOf(unique);
        } catch (IOException | RuntimeException ignored) {
            return List.of();
        }
    }

    public static boolean hasFlyingType(ResourceLocation speciesId) {
        Species species = PokemonSpecies.getByIdentifier(speciesId);
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
