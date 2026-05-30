package nl.streats1.ancientextensions.dex;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Single entry point for setting a player's survey origin.
 */
public final class SurveyOriginService {

    @FunctionalInterface
    public interface OriginAppliedCallback {
        void accept(ServerPlayer player, SurveyRegion region, SurveyOriginTown town, boolean announce);
    }

    private final RegionalSurveyService surveyService;
    private final OriginAppliedCallback onApplied;

    public SurveyOriginService(RegionalSurveyService surveyService, OriginAppliedCallback onApplied) {
        this.surveyService = surveyService;
        this.onApplied = onApplied;
    }

    public boolean hasOrigin(RegionalSurveyData data) {
        return data.getSurveyOrigin().isPresent();
    }

    public boolean trySetOrigin(ServerPlayer player, String regionId, String townId) {
        RegionalSurveyData data = surveyService.get(player);
        if (data.isOriginSetupMode()) {
            return setOrigin(player, regionId, townId, OriginChangePolicy.RECUSTOMIZE, true);
        }
        return setOrigin(player, regionId, townId, OriginChangePolicy.FIRST_CHOICE, true);
    }

    public boolean setOriginAdmin(ServerPlayer player, String regionId) {
        return setOrigin(player, regionId, "", OriginChangePolicy.ADMIN_OVERRIDE, false);
    }

    public void enableOriginSetup(ServerPlayer player) {
        RegionalSurveyData data = surveyService.get(player);
        data.setOriginSetupMode(true);
        surveyService.save(player, data);
    }

    public boolean disableOriginSetup(ServerPlayer player) {
        RegionalSurveyData data = surveyService.get(player);
        if (!data.isOriginSetupMode()) {
            return false;
        }
        data.setOriginSetupMode(false);
        surveyService.save(player, data);
        return true;
    }

    public boolean isOriginSetupMode(ServerPlayer player) {
        return surveyService.get(player).isOriginSetupMode();
    }

    public boolean setOrigin(
            ServerPlayer player,
            String regionId,
            String townId,
            OriginChangePolicy policy,
            boolean announce
    ) {
        RegionalSurveyData data = surveyService.get(player);
        if (policy == OriginChangePolicy.FIRST_CHOICE && hasOrigin(data)) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.origin.already_chosen"));
            return false;
        }
        if (policy == OriginChangePolicy.RECUSTOMIZE && !data.isOriginSetupMode()) {
            return false;
        }

        SurveyRegion region = SurveyRegion.fromId(regionId).orElse(null);
        if (region == null) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.origin.invalid"));
            return false;
        }

        SurveyOriginTown town = null;
        if (policy == OriginChangePolicy.FIRST_CHOICE || policy == OriginChangePolicy.RECUSTOMIZE) {
            town = SurveyOriginTown.fromId(region, townId).orElse(null);
            if (town == null) {
                player.sendSystemMessage(Component.translatable("ancient_extensions.origin.town_invalid"));
                return false;
            }
        } else if (townId != null && !townId.isBlank()) {
            town = SurveyOriginTown.fromId(region, townId).orElse(null);
        }

        data.setSurveyOrigin(region);
        if (town != null) {
            data.setSurveyOriginTown(town);
        } else if (policy == OriginChangePolicy.ADMIN_OVERRIDE) {
            data.clearSurveyOriginTown();
        }
        if (policy == OriginChangePolicy.RECUSTOMIZE) {
            data.setOriginSetupMode(false);
        }
        surveyService.save(player, data);

        if (policy == OriginChangePolicy.RECUSTOMIZE) {
            player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.origin.recustomized_with_town",
                    region.displayName(),
                    town.displayName()
            ));
        } else if (town != null) {
            player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.origin.chosen_with_town",
                    region.displayName(),
                    town.displayName()
            ));
        } else {
            player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.origin.chosen",
                    region.displayName()
            ));
        }
        if (policy == OriginChangePolicy.FIRST_CHOICE) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.origin.chosen_hint"));
        }

        onApplied.accept(player, region, town, announce);
        return true;
    }
}
