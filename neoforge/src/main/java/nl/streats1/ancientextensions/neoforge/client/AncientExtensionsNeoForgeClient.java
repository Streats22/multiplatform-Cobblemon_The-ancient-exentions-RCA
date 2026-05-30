package nl.streats1.ancientextensions.neoforge.client;

import nl.streats1.ancientextensions.client.AncientExtensionsClientHooks;
import nl.streats1.ancientextensions.client.PokeballPouchClient;
import nl.streats1.ancientextensions.client.AncientExtensionsScreens;
import nl.streats1.ancientextensions.network.SelectSurveyRegionPayload;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class AncientExtensionsNeoForgeClient {

    private AncientExtensionsNeoForgeClient() {
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        AncientExtensionsScreens.register(event::register);
    }

    public static void initClientHooks() {
        PokeballPouchClient.registerItemProperties();
        AncientExtensionsClientHooks.setOriginSelectSender((regionId, townId) ->
                PacketDistributor.sendToServer(new SelectSurveyRegionPayload(regionId, townId))
        );
    }
}
