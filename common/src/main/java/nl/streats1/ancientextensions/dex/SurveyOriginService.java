package nl.streats1.ancientextensions.dex;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Single entry point for setting a player's survey origin.
 */
public final class SurveyOriginService {

    @FunctionalInterface
    public interface OriginAppliedCallback {
        void accept(ServerPlayer player, SurveyRegion region, boolean announce);
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

    public boolean trySetOrigin(ServerPlayer player, String regionId) {
        return setOrigin(player, regionId, OriginChangePolicy.FIRST_CHOICE, true);
    }

    public boolean setOriginAdmin(ServerPlayer player, String regionId) {
        return setOrigin(player, regionId, OriginChangePolicy.ADMIN_OVERRIDE, false);
    }

    public boolean setOrigin(
            ServerPlayer player,
            String regionId,
            OriginChangePolicy policy,
            boolean announce
    ) {
        RegionalSurveyData data = surveyService.get(player);
        if (policy == OriginChangePolicy.FIRST_CHOICE && hasOrigin(data)) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.origin.already_chosen"));
            return false;
        }

        SurveyRegion region = SurveyRegion.fromId(regionId).orElse(null);
        if (region == null) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.origin.invalid"));
            return false;
        }

        data.setSurveyOrigin(region);
        surveyService.save(player, data);

        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.origin.chosen",
                region.displayName()
        ));
        if (policy == OriginChangePolicy.FIRST_CHOICE) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.origin.chosen_hint"));
        }

        onApplied.accept(player, region, announce);
        return true;
    }
}
