package nl.streats1.ancientextensions.field;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

import nl.streats1.ancientextensions.migration.*;

/**
 * In-world field readout at a fixed position (season, biome, route, migratory pool).
 */
public record FieldSurveyWorldSnapshot(
        MigrationSeason season,
        MigrationCalendarSource calendarSource,
        ResourceLocation biomeId,
        String biomeName,
        int routeLegCount,
        int primaryLegIndex,
        boolean onActiveRouteLeg,
        int migratorySpeciesCount,
        String migratoryPreview
) {

    public static FieldSurveyWorldSnapshot at(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel server)) {
            return empty();
        }

        MigrationSeason season = MigrationSeasonClock.currentSeason(server);
        MigrationCalendarSource calendar = MigrationSeasonClock.calendarSource();

        Holder<Biome> biome = server.getBiome(pos);
        ResourceLocation biomeId = biome.unwrapKey().map(key -> key.location()).orElse(null);
        String biomeName = MigrationBiomeContext.prettyBiomeName(biomeId);

        List<MigrationLeg> route = MigrationRoutes.routeFor(season);
        int legCount = route.size();
        int legIndex = resolveLegIndex(season, biomeId, route);

        List<ResourceLocation> species = MigrationSpecies.speciesOnRouteForSeason(season);
        String preview = previewSpecies(species, 3);

        return new FieldSurveyWorldSnapshot(
                season,
                calendar,
                biomeId,
                biomeName,
                legCount,
                legIndex,
                legIndex >= 0 && legIndex < legCount && route.get(legIndex).matchesBiome(biomeId),
                species.size(),
                preview
        );
    }

    private static int resolveLegIndex(MigrationSeason season, ResourceLocation biomeId, List<MigrationLeg> route) {
        List<Integer> matches = MigrationBiomeContext.routeLegIndices(season, biomeId);
        if (!matches.isEmpty()) {
            return matches.getFirst();
        }
        return route.isEmpty() ? -1 : 0;
    }

    private static String previewSpecies(List<ResourceLocation> species, int maxNames) {
        if (species.isEmpty()) {
            return "—";
        }
        StringBuilder builder = new StringBuilder();
        int shown = 0;
        for (ResourceLocation id : species) {
            if (shown >= maxNames) {
                break;
            }
            if (shown > 0) {
                builder.append(", ");
            }
            builder.append(shortSpeciesName(id));
            shown++;
        }
        int remaining = species.size() - shown;
        if (remaining > 0) {
            builder.append(" +").append(remaining);
        }
        return builder.toString();
    }

    private static String shortSpeciesName(ResourceLocation id) {
        if (id == null) {
            return "?";
        }
        String path = id.getPath();
        if (path.length() > 10) {
            return path.substring(0, 9) + "…";
        }
        return path;
    }

    private static FieldSurveyWorldSnapshot empty() {
        return new FieldSurveyWorldSnapshot(
                MigrationSeason.SPRING,
                MigrationCalendarSource.INTERNAL_DAYS,
                null,
                "?",
                0,
                -1,
                false,
                0,
                "—"
        );
    }

    public String seasonLine() {
        return season.displayName().getString() + " · " + calendarSource.label().getString();
    }

    public String biomeRouteLine() {
        if (biomeId == null) {
            return "Biome unknown · leg ?/" + routeLegCount;
        }
        int legDisplay = primaryLegIndex >= 0 ? primaryLegIndex + 1 : 1;
        String routeTag = onActiveRouteLeg ? "on route" : "off route";
        return biomeName + " · leg " + legDisplay + "/" + routeLegCount + " · " + routeTag;
    }

    public String speciesLine() {
        return migratorySpeciesCount + " migratory · " + migratoryPreview;
    }
}
