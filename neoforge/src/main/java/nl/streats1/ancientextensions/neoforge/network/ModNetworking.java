package nl.streats1.ancientextensions.neoforge.network;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.menu.PassportMenuOpener;
import nl.streats1.ancientextensions.network.SelectSurveyRegionPayload;
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
            if (AncientExtensionsContext.get().origins().trySetOrigin(serverPlayer, payload.regionId(), payload.townId())) {
                PassportMenuOpener.open(serverPlayer);
            }
        });
    }
}
