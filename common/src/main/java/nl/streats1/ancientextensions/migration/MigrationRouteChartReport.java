package nl.streats1.ancientextensions.migration;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

/** Builds a field chart book for the player's current biome and seasonal route. */
public final class MigrationRouteChartReport {

    private static final int PAGE_CHAR_LIMIT = 220;

    private MigrationRouteChartReport() {
    }

    /** Lines for the migration route chart screen (current biome, legs, species). */
    public static List<Component> buildLines(ServerPlayer player) {
        MigrationSeason season = MigrationSeasonClock.currentSeason(player.serverLevel());
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        data.syncMigrationSeason(season);
        ResourceLocation biomeId = MigrationBiomeContext.currentBiomeId(player);

        List<Component> lines = new ArrayList<>();
        appendBlock(lines, titleLine(season, data));
        appendBlock(lines, hereLine(biomeId, season, data));
        for (Component legBlock : legBlocks(season, data)) {
            appendBlock(lines, legBlock);
        }
        appendBlock(lines, speciesLine(season));
        return List.copyOf(lines);
    }

    private static void appendBlock(List<Component> lines, Component block) {
        if (!lines.isEmpty()) {
            lines.add(Component.empty());
        }
        for (String part : block.getString().split("\n", -1)) {
            if (part.isEmpty()) {
                lines.add(Component.empty());
            } else {
                lines.add(Component.literal(part).withStyle(block.getStyle()));
            }
        }
    }

    private static Component titleLine(MigrationSeason season, RegionalSurveyData data) {
        List<MigrationLeg> route = MigrationRoutes.routeFor(season);
        int legDisplay = Math.min(data.getMigrationLegIndex() + 1, route.size());
        return Component.translatable(
                        "ancient_extensions.migration_chart.title",
                        season.displayName(),
                        legDisplay,
                        route.size(),
                        data.getCurrentLegCatches(),
                        data.getMigrationCompletions(season)
                )
                .withStyle(ChatFormatting.BLACK);
    }

    private static Component hereLine(ResourceLocation biomeId, MigrationSeason season, RegionalSurveyData data) {
        String biomeName = MigrationBiomeContext.prettyBiomeName(biomeId);
        int activeLeg = data.getMigrationLegIndex();
        List<MigrationLeg> route = MigrationRoutes.routeFor(season);

        if (biomeId == null) {
            return Component.translatable("ancient_extensions.migration_chart.here_unknown")
                    .withStyle(ChatFormatting.BLACK);
        }
        if (MigrationBiomeContext.isVanillaBiome(biomeId)) {
            if (activeLeg >= route.size()) {
                return Component.translatable(
                                "ancient_extensions.migration_chart.here_vanilla_done",
                                biomeName
                        )
                        .withStyle(ChatFormatting.BLACK);
            }
            return Component.translatable(
                            "ancient_extensions.migration_chart.here_vanilla_active",
                            biomeName,
                            activeLeg + 1
                    )
                    .withStyle(ChatFormatting.BLACK);
        }

        List<Integer> legs = MigrationBiomeContext.routeLegIndices(season, biomeId);
        if (legs.isEmpty()) {
            return Component.translatable(
                            "ancient_extensions.migration_chart.here_off_route",
                            biomeName
                    )
                    .withStyle(ChatFormatting.BLACK);
        }

        if (legs.contains(activeLeg) && activeLeg < route.size()) {
            MigrationLeg leg = route.get(activeLeg);
            return Component.translatable(
                            "ancient_extensions.migration_chart.here_on_active_leg",
                            biomeName,
                            activeLeg + 1,
                            data.getCurrentLegCatches(),
                            leg.requiredCatches()
                    )
                    .withStyle(ChatFormatting.BLACK);
        }

        int nearest = legs.getFirst();
        return Component.translatable(
                        "ancient_extensions.migration_chart.here_other_leg",
                        biomeName,
                        nearest + 1,
                        activeLeg + 1
                )
                .withStyle(ChatFormatting.BLACK);
    }

    private static List<Component> legBlocks(MigrationSeason season, RegionalSurveyData data) {
        List<Component> blocks = new ArrayList<>();
        List<MigrationLeg> route = MigrationRoutes.routeFor(season);
        int activeLeg = data.getMigrationLegIndex();

        for (int index = 0; index < route.size(); index++) {
            MigrationLeg leg = route.get(index);
            String marker;
            if (index < activeLeg || activeLeg >= route.size()) {
                marker = "[x]";
            } else if (index == activeLeg) {
                marker = "[>]";
            } else {
                marker = "[ ]";
            }

            String header = Component.translatable(
                    "ancient_extensions.migration_chart.leg_header",
                    marker,
                    index + 1,
                    leg.requiredCatches(),
                    leg.bonusResearchPoints()
            ).getString();

            List<String> biomeLines = formatBiomeLines(leg.biomeIds());
            StringBuilder chunk = new StringBuilder(header);
            for (String line : biomeLines) {
                if (chunk.length() + line.length() + 1 > PAGE_CHAR_LIMIT) {
                    blocks.add(Component.literal(chunk.toString()).withStyle(ChatFormatting.BLACK));
                    chunk = new StringBuilder(line);
                } else {
                    if (!chunk.isEmpty()) {
                        chunk.append('\n');
                    }
                    chunk.append(line);
                }
            }
            blocks.add(Component.literal(chunk.toString()).withStyle(ChatFormatting.BLACK));
        }
        return blocks;
    }

    private static List<String> formatBiomeLines(List<ResourceLocation> biomeIds) {
        List<String> lines = new ArrayList<>();
        StringBuilder row = new StringBuilder();
        for (ResourceLocation id : biomeIds) {
            String name = MigrationBiomeContext.prettyBiomeName(id);
            if (row.isEmpty()) {
                row.append("• ").append(name);
            } else if (row.length() + name.length() + 3 <= 46) {
                row.append(" · ").append(name);
            } else {
                lines.add(row.toString());
                row = new StringBuilder("• ").append(name);
            }
        }
        if (!row.isEmpty()) {
            lines.add(row.toString());
        }
        lines.add(Component.translatable("ancient_extensions.migration_chart.vanilla_note").getString());
        return lines;
    }

    private static Component speciesLine(MigrationSeason season) {
        StringBuilder surveyNames = new StringBuilder();
        for (ResourceLocation id : MigrationSpecies.speciesForSeason(season)) {
            if (!surveyNames.isEmpty()) {
                surveyNames.append(" · ");
            }
            surveyNames.append(formatSpecies(id));
        }
        return Component.translatable(
                        "ancient_extensions.migration_chart.species",
                        season.displayName(),
                        surveyNames.toString()
                )
                .withStyle(ChatFormatting.BLACK);
    }

    private static String formatSpecies(ResourceLocation id) {
        String path = id.getPath();
        if (path.contains("/")) {
            path = path.substring(path.lastIndexOf('/') + 1);
        }
        return MigrationBiomeContext.prettyBiomeName(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path));
    }

}
