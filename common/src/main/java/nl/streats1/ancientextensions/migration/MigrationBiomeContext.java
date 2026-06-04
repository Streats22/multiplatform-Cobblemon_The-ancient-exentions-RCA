package nl.streats1.ancientextensions.migration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Holder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Resolves player biome context against seasonal migration routes. */
public final class MigrationBiomeContext {

    private MigrationBiomeContext() {
    }

    public static ResourceLocation currentBiomeId(ServerPlayer player) {
        Holder<Biome> biome = player.serverLevel().getBiome(player.blockPosition());
        return biome.unwrapKey().map(key -> key.location()).orElse(null);
    }

    public static boolean isVanillaBiome(ResourceLocation biomeId) {
        return biomeId != null && "minecraft".equals(biomeId.getNamespace());
    }

    public static boolean isRegionsUnexploredBiome(ResourceLocation biomeId) {
        return RegionsUnexploredBiomes.isRegionsUnexplored(biomeId);
    }

    public static boolean isBiomesOPlentyBiome(ResourceLocation biomeId) {
        return BiomesOPlentyBiomes.isBiomesOPlenty(biomeId);
    }

    /** Leg indices (0-based) where this biome counts for the route (includes vanilla). */
    public static List<Integer> routeLegIndices(MigrationSeason season, ResourceLocation biomeId) {
        if (biomeId == null) {
            return List.of();
        }
        List<MigrationLeg> route = MigrationRoutes.routeFor(season);
        List<Integer> matches = new ArrayList<>();
        for (int index = 0; index < route.size(); index++) {
            if (route.get(index).matchesBiome(biomeId)) {
                matches.add(index);
            }
        }
        return List.copyOf(matches);
    }

    public static boolean countsForLeg(MigrationSeason season, ResourceLocation biomeId, int legIndex) {
        List<MigrationLeg> route = MigrationRoutes.routeFor(season);
        if (legIndex < 0 || legIndex >= route.size()) {
            return false;
        }
        return route.get(legIndex).matchesBiome(biomeId);
    }

    public static String prettyBiomeName(ResourceLocation id) {
        if (id == null) {
            return "?";
        }
        String path = id.getPath().replace('_', ' ');
        String[] words = path.split(" ");
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                name.append(' ');
            }
            String word = words[i];
            if (!word.isEmpty()) {
                name.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    name.append(word.substring(1).toLowerCase(Locale.ROOT));
                }
            }
        }
        return name.toString();
    }
}
