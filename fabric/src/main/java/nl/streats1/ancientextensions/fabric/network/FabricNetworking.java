package nl.streats1.ancientextensions.fabric.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.integration.map.MapWaypointNetworking;
import nl.streats1.ancientextensions.menu.PassportMenuOpener;
import nl.streats1.ancientextensions.network.*;

public final class FabricNetworking {

    private FabricNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(MigrationWaypointPayload.TYPE, MigrationWaypointPayload.STREAM_CODEC);
        MapWaypointNetworking.setSender(ServerPlayNetworking::send);

        PayloadTypeRegistry.playC2S().register(SelectSurveyRegionPayload.TYPE, SelectSurveyRegionPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ClaimTierRewardPayload.TYPE, ClaimTierRewardPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ClaimShinyCharmPayload.TYPE, ClaimShinyCharmPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TabletActionPayload.TYPE, TabletActionPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SelectSurveyRegionPayload.TYPE, (payload, context) ->
                context.server().execute(() -> {
                    if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                        return;
                    }
                    if (AncientExtensionsContext.get().origins().trySetOrigin(serverPlayer, payload.regionId(), payload.townId())) {
                        PassportMenuOpener.open(serverPlayer);
                    }
                })
        );
        ServerPlayNetworking.registerGlobalReceiver(ClaimTierRewardPayload.TYPE, (payload, context) ->
                context.server().execute(() -> {
                    if (context.player() instanceof ServerPlayer serverPlayer) {
                        TierRewardNetworking.handleClaim(serverPlayer, payload);
                    }
                })
        );
        ServerPlayNetworking.registerGlobalReceiver(ClaimShinyCharmPayload.TYPE, (payload, context) ->
                context.server().execute(() -> {
                    if (context.player() instanceof ServerPlayer serverPlayer) {
                        ShinyCharmNetworking.handleClaim(serverPlayer);
                    }
                })
        );
        ServerPlayNetworking.registerGlobalReceiver(TabletActionPayload.TYPE, (payload, context) ->
                context.server().execute(() -> {
                    if (context.player() instanceof ServerPlayer serverPlayer) {
                        TabletNetworking.handle(serverPlayer, payload);
                    }
                })
        );
    }

    public static void openPassport(ServerPlayer player) {
        PassportMenuOpener.open(player);
    }
}
