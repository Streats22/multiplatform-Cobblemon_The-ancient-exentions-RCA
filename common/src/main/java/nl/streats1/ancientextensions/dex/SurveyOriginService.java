package nl.streats1.ancientextensions.dex;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Locks in the player's survey origin region (first choice only).
 */
public final class SurveyOriginService {

    private SurveyOriginService() {
    }

    public static boolean hasOrigin(RegionalSurveyData data) {
        return data.getSurveyOrigin().isPresent();
    }

    public static boolean trySetOrigin(ServerPlayer player, String regionId) {
        RegionalSurveyData data = RegionalSurveyService.get(player);
        if (hasOrigin(data)) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.origin.already_chosen"));
            return false;
        }

        SurveyRegion region = SurveyRegion.fromId(regionId).orElse(null);
        if (region == null) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.origin.invalid"));
            return false;
        }

        data.setSurveyOrigin(region);
        RegionalSurveyService.save(player, data);
        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.origin.chosen",
                region.displayName()
        ));
        player.sendSystemMessage(Component.translatable("ancient_extensions.origin.chosen_hint"));
        return true;
    }
}
