package com.streats.ancientextensions.dex;

import com.streats.ancientextensions.migration.MigrationLeg;
import com.streats.ancientextensions.migration.MigrationRoutes;
import com.streats.ancientextensions.migration.MigrationSeason;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class SurveyGoals {

    public static final int CATCH_GOAL_COUNT = 3;

    private SurveyGoals() {
    }

    public static List<SurveyGoal> build(RegionalSurveyData data, MigrationSeason season) {
        List<SurveyGoal> goals = new ArrayList<>();
        var route = MigrationRoutes.routeFor(season);

        goals.add(new SurveyGoal(
                "pitch_camp",
                Component.translatable("ancient_extensions.journal.goal.pitch_camp"),
                data.hasDeployedProfessorsKit(),
                Component.empty()
        ));

        int caught = data.getCaughtSpeciesCount();
        goals.add(new SurveyGoal(
                "catch_three",
                Component.translatable("ancient_extensions.journal.goal.catch_three"),
                caught >= CATCH_GOAL_COUNT,
                Component.translatable("ancient_extensions.journal.progress.catch", caught, CATCH_GOAL_COUNT)
        ));

        int legIndex = data.getMigrationLegIndex();
        int legCatches = data.getCurrentLegCatches();
        boolean routeDoneThisSeason = data.getMigrationCompletions(season) > 0;
        boolean onRoute = legIndex < route.size();
        Component migrationLabel = Component.translatable(
                "ancient_extensions.journal.goal.migration_route",
                season.displayName()
        );
        Component migrationProgress;
        if (routeDoneThisSeason) {
            migrationProgress = Component.translatable(
                    "ancient_extensions.journal.progress.migration_done",
                    data.getMigrationCompletions(season)
            );
        } else if (onRoute) {
            MigrationLeg leg = route.get(legIndex);
            migrationProgress = Component.translatable(
                    "ancient_extensions.journal.progress.migration_leg",
                    legIndex + 1,
                    route.size(),
                    legCatches,
                    leg.requiredCatches()
            );
        } else {
            migrationProgress = Component.translatable("ancient_extensions.journal.progress.migration_ready");
        }
        goals.add(new SurveyGoal(
                "migration_route",
                migrationLabel,
                routeDoneThisSeason,
                migrationProgress
        ));

        ResearchTier tier = data.getTier();
        ResearchTier next = nextTier(tier);
        boolean tierGoalDone = next == null || data.getResearchPoints() >= next.minPoints();
        goals.add(new SurveyGoal(
                "next_tier",
                next == null
                        ? Component.translatable("ancient_extensions.journal.goal.max_tier")
                        : Component.translatable("ancient_extensions.journal.goal.reach_tier", next.displayName()),
                tierGoalDone,
                next == null
                        ? Component.empty()
                        : Component.translatable(
                                "ancient_extensions.journal.progress.rp",
                                data.getResearchPoints(),
                                next.minPoints()
                        )
        ));

        return goals;
    }

    private static ResearchTier nextTier(ResearchTier current) {
        ResearchTier[] tiers = ResearchTier.values();
        for (int i = 0; i < tiers.length - 1; i++) {
            if (tiers[i] == current) {
                return tiers[i + 1];
            }
        }
        return null;
    }
}
