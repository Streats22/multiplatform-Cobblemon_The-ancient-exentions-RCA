package nl.streats1.ancientextensions.fabric.network;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.menu.PassportMenuOpener;
import nl.streats1.ancientextensions.network.ClaimTierRewardPayload;
import nl.streats1.ancientextensions.network.SelectSurveyRegionPayload;
import nl.streats1.ancientextensions.network.TierRewardNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class FabricNetworking {

    private FabricNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(SelectSurveyRegionPayload.TYPE, SelectSurveyRegionPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ClaimTierRewardPayload.TYPE, ClaimTierRewardPayload.STREAM_CODEC);
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
    }

    public static void openPassport(ServerPlayer player) {
        PassportMenuOpener.open(player);
    }
}
