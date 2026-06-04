package nl.streats1.ancientextensions.migration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parsed migratory spawn pools for calendar estimates (biome + weight), not live world simulation.
 */
public final class MigrationSpawnPoolIndex {

    private static Map<MigrationSeason, List<WeightedSpawn>> pools;

    private MigrationSpawnPoolIndex() {
    }

    private static Map<MigrationSeason, List<WeightedSpawn>> pools() {
        if (pools == null) {
            pools = filterForActiveMods(loadAll());
        }
        return pools;
    }

    public record WeightedSpawn(ResourceLocation speciesId, float weight, Set<ResourceLocation> biomes) {

        public boolean matchesBiome(ResourceLocation biomeId) {
            if (biomeId == null || biomes.isEmpty()) {
                return false;
            }
            return biomes.contains(biomeId);
        }
    }

    public record SpawnEstimate(ResourceLocation speciesId, float weight, EstimateTier tier) {
    }

    public enum EstimateTier {
        HIGH,
        LIKELY,
        POSSIBLE
    }

    /** Species ranked for this biome in the current season (spawn-pool weights). */
    public static List<SpawnEstimate> estimateForBiome(MigrationSeason season, ResourceLocation biomeId, int limit) {
        List<WeightedSpawn> pool = pools().getOrDefault(season, List.of());
        if (pool.isEmpty()) {
            return List.of();
        }

        boolean vanilla = biomeId != null && MigrationBiomeContext.isVanillaBiome(biomeId);
        Map<ResourceLocation, Float> bestWeight = new HashMap<>();

        for (WeightedSpawn spawn : pool) {
            if (vanilla || (biomeId != null && spawn.matchesBiome(biomeId))) {
                bestWeight.merge(spawn.speciesId(), spawn.weight(), Math::max);
            }
        }

        List<SpawnEstimate> ranked = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Float> entry : bestWeight.entrySet()) {
            float weight = entry.getValue();
            ranked.add(new SpawnEstimate(entry.getKey(), weight, tierFor(weight)));
        }

        ranked.sort(Comparator.comparingDouble((SpawnEstimate e) -> e.weight()).reversed());
        if (ranked.size() > limit) {
            return List.copyOf(ranked.subList(0, limit));
        }
        return List.copyOf(ranked);
    }

    private static EstimateTier tierFor(float weight) {
        if (weight >= 1.5f) {
            return EstimateTier.HIGH;
        }
        if (weight >= 1.0f) {
            return EstimateTier.LIKELY;
        }
        return EstimateTier.POSSIBLE;
    }

    private static Map<MigrationSeason, List<WeightedSpawn>> filterForActiveMods(
            Map<MigrationSeason, List<WeightedSpawn>> loaded
    ) {
        Set<String> active = MigrationBiomeCatalog.activeSpawnNamespaces();
        Map<MigrationSeason, List<WeightedSpawn>> filtered = new EnumMap<>(MigrationSeason.class);
        for (Map.Entry<MigrationSeason, List<WeightedSpawn>> entry : loaded.entrySet()) {
            List<WeightedSpawn> seasonPool = new ArrayList<>();
            for (WeightedSpawn spawn : entry.getValue()) {
                Set<ResourceLocation> biomes = new HashSet<>();
                for (ResourceLocation biome : spawn.biomes()) {
                    if (active.contains(biome.getNamespace())) {
                        biomes.add(biome);
                    }
                }
                if (!biomes.isEmpty()) {
                    seasonPool.add(new WeightedSpawn(spawn.speciesId(), spawn.weight(), Set.copyOf(biomes)));
                }
            }
            filtered.put(entry.getKey(), List.copyOf(seasonPool));
        }
        return Map.copyOf(filtered);
    }

    private static Map<MigrationSeason, List<WeightedSpawn>> loadAll() {
        Map<MigrationSeason, List<WeightedSpawn>> loaded = new EnumMap<>(MigrationSeason.class);
        for (MigrationSeason season : MigrationSeason.values()) {
            loaded.put(season, loadSeason(season));
        }
        return loaded;
    }

    private static List<WeightedSpawn> loadSeason(MigrationSeason season) {
        String path = "/data/cobblemon/spawn_pool_world/ancient_extensions_migration_" + season.getId() + ".json";
        try (InputStream stream = MigrationSpawnPoolIndex.class.getResourceAsStream(path)) {
            if (stream == null) {
                return List.of();
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            JsonArray spawns = root.getAsJsonArray("spawns");
            List<WeightedSpawn> entries = new ArrayList<>();
            for (JsonElement element : spawns) {
                JsonObject spawn = element.getAsJsonObject();
                String pokemon = spawn.get("pokemon").getAsString();
                float weight = spawn.has("weight") ? spawn.get("weight").getAsFloat() : 1.0f;
                Set<ResourceLocation> biomes = readBiomes(spawn);
                entries.add(new WeightedSpawn(
                        ResourceLocation.fromNamespaceAndPath("cobblemon", pokemon),
                        weight,
                        biomes
                ));
            }
            return List.copyOf(entries);
        } catch (IOException | RuntimeException ignored) {
            return List.of();
        }
    }

    private static Set<ResourceLocation> readBiomes(JsonObject spawn) {
        Set<ResourceLocation> biomes = new HashSet<>();
        if (!spawn.has("condition")) {
            return biomes;
        }
        JsonObject condition = spawn.getAsJsonObject("condition");
        if (!condition.has("biomes")) {
            return biomes;
        }
        for (JsonElement biome : condition.getAsJsonArray("biomes")) {
            biomes.add(ResourceLocation.parse(biome.getAsString()));
        }
        return biomes;
    }
}
