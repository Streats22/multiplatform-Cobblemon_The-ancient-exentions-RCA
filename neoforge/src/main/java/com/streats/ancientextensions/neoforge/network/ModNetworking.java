package com.streats.ancientextensions.neoforge.network;

import com.streats.ancientextensions.dex.RegionalSurveyData;
import com.streats.ancientextensions.dex.RegionalSurveyService;
import com.streats.ancientextensions.dex.SurveyOriginService;
import com.streats.ancientextensions.dex.SurveyRegion;
import com.streats.ancientextensions.dex.SurveyOriginHooks;
import com.streats.ancientextensions.neoforge.passport.PassportMenuOpener;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ModNetworking {

    private ModNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(
                SelectSurveyRegionPayload.TYPE,
                SelectSurveyRegionPayload.STREAM_CODEC,
                ModNetworking::handleSelectSurveyRegion
        );
    }

    public static void openPassport(ServerPlayer player) {
        PassportMenuOpener.open(player);
    }

    private static void handleSelectSurveyRegion(SelectSurveyRegionPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        context.enqueueWork(() -> {
            if (!SurveyOriginService.trySetOrigin(serverPlayer, payload.regionId())) {
                return;
            }
            SurveyRegion region = SurveyRegion.fromId(payload.regionId()).orElse(null);
            if (region == null) {
                return;
            }
            SurveyOriginHooks.notifyApplied(serverPlayer, region, true);
            PassportMenuOpener.open(serverPlayer);
        });
    }

    public static void promptOriginIfNeeded(ServerPlayer player) {
        RegionalSurveyData data = RegionalSurveyService.get(player);
        if (!SurveyOriginService.hasOrigin(data)) {
            openPassport(player);
        }
    }
}
