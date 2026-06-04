package nl.streats1.ancientextensions.dex;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.migration.MigrationBiomeContext;
import nl.streats1.ancientextensions.migration.MigrationLeg;
import nl.streats1.ancientextensions.migration.MigrationRoutes;
import nl.streats1.ancientextensions.migration.MigrationSeason;
import nl.streats1.ancientextensions.migration.MigrationSeasonClock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

/** Compact live dashboard for the Field Survey Tablet. */
public final class FieldSurveyTabletReport {

    private static final int MAX_GOALS = 4;

    private FieldSurveyTabletReport() {
    }

    public static List<Component> buildLines(ServerPlayer player) {
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        MigrationSeason season = MigrationSeasonClock.currentSeason(player.serverLevel());
        data.syncMigrationSeason(season);

        List<Component> lines = new ArrayList<>();
        ResearchTier tier = data.getTier();
        TierRewardService rewards = AncientExtensionsContext.get().tierRewards();

        lines.add(Component.translatable("ancient_extensions.tablet.section_status")
                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD));
        lines.add(Component.translatable(
                        "ancient_extensions.tablet.stats",
                        data.getCaughtSpeciesCount(),
                        data.getResearchPoints(),
                        tier.displayName()
                )
                .withStyle(ChatFormatting.DARK_GRAY));

        data.getSurveyOrigin().ifPresentOrElse(
                region -> lines.add(Component.translatable(
                                "ancient_extensions.tablet.origin",
                                region.labeledName()
                        )
                        .withStyle(ChatFormatting.DARK_GRAY)),
                () -> lines.add(Component.translatable("ancient_extensions.tablet.origin_pending")
                        .withStyle(ChatFormatting.DARK_GRAY))
        );

        lines.add(Component.empty());
        lines.add(Component.translatable("ancient_extensions.tablet.section_field")
                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD));
        lines.add(fieldLine(player, data, season));

        lines.add(Component.empty());
        lines.add(Component.translatable("ancient_extensions.tablet.section_goals")
                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD));
        int shown = 0;
        for (SurveyGoal goal : SurveyGoals.build(data, season)) {
            if (shown >= MAX_GOALS) {
                break;
            }
            lines.add(goal.statusLine());
            shown++;
        }

        int unclaimed = rewards.unclaimedCount(data);
        if (unclaimed > 0) {
            lines.add(Component.empty());
            lines.add(Component.translatable("ancient_extensions.tablet.rewards_ready", unclaimed)
                    .withStyle(ChatFormatting.DARK_GREEN));
        }

        return List.copyOf(lines);
    }

    private static Component fieldLine(ServerPlayer player, RegionalSurveyData data, MigrationSeason season) {
        ResourceLocation biomeId = MigrationBiomeContext.currentBiomeId(player);
        List<MigrationLeg> route = MigrationRoutes.routeFor(season);
        int legDisplay = Math.min(data.getMigrationLegIndex() + 1, route.size());

        if (biomeId == null) {
            return Component.translatable(
                            "ancient_extensions.tablet.field_unknown",
                            season.displayName(),
                            legDisplay,
                            route.size()
                    )
                    .withStyle(ChatFormatting.DARK_GRAY);
        }

        String biomeName = MigrationBiomeContext.prettyBiomeName(biomeId);
        return Component.translatable(
                        "ancient_extensions.tablet.field",
                        season.displayName(),
                        legDisplay,
                        route.size(),
                        data.getCurrentLegCatches(),
                        biomeName
                )
                .withStyle(ChatFormatting.DARK_GRAY);
    }

}
