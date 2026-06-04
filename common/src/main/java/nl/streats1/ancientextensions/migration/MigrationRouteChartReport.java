package nl.streats1.ancientextensions.migration;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;

/**
 * Builds a field chart book for the player's current biome and seasonal route.
 */
public final class MigrationRouteChartReport {

    private static final int PAGE_CHAR_LIMIT = 220;

    private MigrationRouteChartReport() {
    }

    private static final ChatFormatting BODY = ChatFormatting.DARK_GRAY;
    private static final ChatFormatting SECTION = ChatFormatting.DARK_AQUA;
    private static final ChatFormatting EMPHASIS = ChatFormatting.DARK_GREEN;

    /**
     * Lines for the migration route chart screen (current biome, legs, species).
     */
    public static List<Component> buildLines(ServerPlayer player) {
        MigrationSeason season = MigrationSeasonClock.currentSeason(player.serverLevel());
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        data.syncMigrationSeason(season);
        ResourceLocation biomeId = MigrationBiomeContext.currentBiomeId(player);

        List<Component> lines = new ArrayList<>();
        appendGuide(lines);
        appendBlock(lines, titleLine(season, data));
        appendBlock(lines, hereLine(biomeId, season, data));
        for (Component legBlock : legBlocks(season, data)) {
            appendBlock(lines, legBlock);
        }
        for (Component speciesBlock : speciesBlocks(season)) {
            appendBlock(lines, speciesBlock);
        }
        return List.copyOf(lines);
    }

    private static void appendGuide(List<Component> lines) {
        lines.add(Component.translatable("ancient_extensions.migration_chart.guide_title")
                .withStyle(SECTION, ChatFormatting.BOLD));
        for (int i = 1; i <= 4; i++) {
            lines.add(Component.translatable("ancient_extensions.migration_chart.guide_line" + i)
                    .withStyle(BODY));
        }
        lines.add(Component.empty());
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
                .withStyle(BODY);
    }

    private static Component hereLine(ResourceLocation biomeId, MigrationSeason season, RegionalSurveyData data) {
        String biomeName = MigrationBiomeContext.prettyBiomeName(biomeId);
        int activeLeg = data.getMigrationLegIndex();
        List<MigrationLeg> route = MigrationRoutes.routeFor(season);

        if (biomeId == null) {
            return Component.translatable("ancient_extensions.migration_chart.here_unknown")
                    .withStyle(BODY);
        }
        if (MigrationBiomeContext.isVanillaBiome(biomeId)) {
            if (activeLeg >= route.size()) {
                return Component.translatable(
                                "ancient_extensions.migration_chart.here_vanilla_done",
                                biomeName
                        )
                        .withStyle(BODY);
            }
            return Component.translatable(
                            "ancient_extensions.migration_chart.here_vanilla_active",
                            biomeName,
                            activeLeg + 1
                    )
                    .withStyle(EMPHASIS);
        }

        List<Integer> legs = MigrationBiomeContext.routeLegIndices(season, biomeId);
        if (legs.isEmpty()) {
            return Component.translatable(
                            "ancient_extensions.migration_chart.here_off_route",
                            biomeName
                    )
                    .withStyle(BODY);
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
                    .withStyle(EMPHASIS);
        }

        int nearest = legs.getFirst();
        return Component.translatable(
                        "ancient_extensions.migration_chart.here_other_leg",
                        biomeName,
                        nearest + 1,
                        activeLeg + 1
                )
                .withStyle(BODY);
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
                    blocks.add(Component.literal(chunk.toString()).withStyle(BODY));
                    chunk = new StringBuilder(line);
                } else {
                    if (!chunk.isEmpty()) {
                        chunk.append('\n');
                    }
                    chunk.append(line);
                }
            }
            blocks.add(Component.literal(chunk.toString()).withStyle(BODY));
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

    private static List<Component> speciesBlocks(MigrationSeason season) {
        List<ResourceLocation> species = MigrationSpecies.speciesOnRouteForSeason(season);
        List<Component> blocks = new ArrayList<>();
        if (species.isEmpty()) {
            blocks.add(
                    Component.translatable("ancient_extensions.migration_chart.species_empty", season.displayName())
                            .withStyle(BODY)
            );
            return blocks;
        }

        String header = Component.translatable(
                "ancient_extensions.migration_chart.species_header",
                season.displayName()
        ).getString();
        StringBuilder chunk = new StringBuilder(header);
        for (ResourceLocation id : species) {
            String name = formatSpecies(id);
            if (chunk.length() + name.length() + 3 > PAGE_CHAR_LIMIT) {
                blocks.add(Component.literal(chunk.toString()).withStyle(BODY));
                chunk = new StringBuilder(name);
            } else if (chunk.length() == header.length()) {
                chunk.append('\n').append(name);
            } else {
                chunk.append(" · ").append(name);
            }
        }
        if (!chunk.isEmpty()) {
            blocks.add(Component.literal(chunk.toString()).withStyle(BODY));
        }
        return blocks;
    }

    private static String formatSpecies(ResourceLocation id) {
        String path = id.getPath();
        if (path.contains("/")) {
            path = path.substring(path.lastIndexOf('/') + 1);
        }
        return MigrationBiomeContext.prettyBiomeName(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path));
    }

}
