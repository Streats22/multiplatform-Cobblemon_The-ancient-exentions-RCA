package nl.streats1.ancientextensions.migration;

import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.dex.RegionalSurveyService;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class MigrationService {

    private final RegionalSurveyService surveyService;

    public MigrationService(RegionalSurveyService surveyService) {
        this.surveyService = surveyService;
    }

    public void onSpeciesCaptured(ServerPlayer player, ResourceLocation speciesId) {
        ServerLevel level = player.serverLevel();
        MigrationSeason season = MigrationSeasonClock.currentSeason(level);
        RegionalSurveyData data = surveyService.get(player);
        data.syncMigrationSeason(season);

        if (!MigrationSpecies.isMigratory(season, speciesId)) {
            return;
        }

        List<MigrationLeg> route = MigrationRoutes.routeFor(season);
        if (data.getMigrationLegIndex() >= route.size()) {
            return;
        }

        ResourceLocation biomeId = MigrationBiomeContext.currentBiomeId(player);
        MigrationLeg leg = route.get(data.getMigrationLegIndex());
        if (!leg.matchesBiome(biomeId)) {
            return;
        }

        data.recordLegCatch();
        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.migration.leg_progress",
                leg.biomeLabel(),
                speciesId.toString(),
                data.getCurrentLegCatches(),
                leg.requiredCatches()
        ));

        if (data.getCurrentLegCatches() < leg.requiredCatches()) {
            surveyService.save(player, data);
            return;
        }

        data.addResearchPoints(leg.bonusResearchPoints());
        data.advanceMigrationLeg();
        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.migration.leg_complete",
                data.getMigrationLegIndex(),
                route.size()
        ));

        if (data.getMigrationLegIndex() >= route.size()) {
            finishRoute(player, data, season);
        }

        surveyService.save(player, data);
    }

    private void finishRoute(ServerPlayer player, RegionalSurveyData data, MigrationSeason season) {
        int prior = data.getMigrationCompletions(season);
        int reward = MigrationConfig.routeCompletionReward(prior);
        data.recordMigrationCompletion(season, reward);
        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.migration.route_complete",
                season.displayName(),
                reward,
                prior + 1
        ));
        if (prior > 0) {
            player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.migration.diminished_repeat",
                    prior + 1
            ));
        }
    }

}
