package nl.streats1.ancientextensions.neoforge.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.menu.PassportMenuOpener;
import nl.streats1.ancientextensions.network.*;

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
        registrar.playToServer(
                ClaimTierRewardPayload.TYPE,
                ClaimTierRewardPayload.STREAM_CODEC,
                ModNetworking::handleClaimTierReward
        );
        registrar.playToServer(
                TabletActionPayload.TYPE,
                TabletActionPayload.STREAM_CODEC,
                ModNetworking::handleTabletAction
        );
    }

    public static void openPassport(ServerPlayer player) {
        PassportMenuOpener.open(player);
    }

    private static void handleSelectSurveyRegion(SelectSurveyRegionPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        serverPlayer.server.execute(() -> {
            if (AncientExtensionsContext.get().origins().trySetOrigin(serverPlayer, payload.regionId(), payload.townId())) {
                PassportMenuOpener.open(serverPlayer);
            }
        });
    }

    private static void handleClaimTierReward(ClaimTierRewardPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        serverPlayer.server.execute(() -> TierRewardNetworking.handleClaim(serverPlayer, payload));
    }

    private static void handleTabletAction(TabletActionPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        serverPlayer.server.execute(() -> TabletNetworking.handle(serverPlayer, payload));
    }
}
