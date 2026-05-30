package nl.streats1.ancientextensions.neoforge.network;

import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.dex.RegionalSurveyService;
import nl.streats1.ancientextensions.dex.SurveyOriginService;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.dex.SurveyOriginHooks;
import nl.streats1.ancientextensions.neoforge.passport.PassportMenuOpener;
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
