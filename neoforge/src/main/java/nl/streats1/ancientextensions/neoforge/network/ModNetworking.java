package nl.streats1.ancientextensions.neoforge.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.client.MigrationWaypointClient;
import nl.streats1.ancientextensions.integration.map.MapWaypointNetworking;
import nl.streats1.ancientextensions.menu.PassportMenuOpener;
import nl.streats1.ancientextensions.network.*;

public final class ModNetworking {

    private ModNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        MapWaypointNetworking.setSender((player, payload) ->
                PacketDistributor.sendToPlayer(player, payload)
        );

        var registrar = event.registrar("1");
        registrar.playToClient(
                MigrationWaypointPayload.TYPE,
                MigrationWaypointPayload.STREAM_CODEC,
                ModNetworking::handleMigrationWaypoint
        );
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
                ClaimShinyCharmPayload.TYPE,
                ClaimShinyCharmPayload.STREAM_CODEC,
                ModNetworking::handleClaimShinyCharm
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

    private static void handleClaimShinyCharm(ClaimShinyCharmPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        serverPlayer.server.execute(() -> ShinyCharmNetworking.handleClaim(serverPlayer));
    }

    private static void handleTabletAction(TabletActionPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        serverPlayer.server.execute(() -> TabletNetworking.handle(serverPlayer, payload));
    }

    private static void handleMigrationWaypoint(MigrationWaypointPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> MigrationWaypointClient.handle(payload));
    }
}
