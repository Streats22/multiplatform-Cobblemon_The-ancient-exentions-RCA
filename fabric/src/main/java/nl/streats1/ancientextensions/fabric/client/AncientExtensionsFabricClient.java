package nl.streats1.ancientextensions.fabric.client;

import nl.streats1.ancientextensions.client.AncientExtensionsClientHooks;
import nl.streats1.ancientextensions.client.AncientExtensionsScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import nl.streats1.ancientextensions.network.SelectSurveyRegionPayload;
import net.minecraft.client.gui.screens.MenuScreens;

public class AncientExtensionsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playC2S().register(SelectSurveyRegionPayload.TYPE, SelectSurveyRegionPayload.STREAM_CODEC);
        AncientExtensionsScreens.register(MenuScreens::register);
        AncientExtensionsClientHooks.setRegionSelectSender(regionId ->
                ClientPlayNetworking.send(new SelectSurveyRegionPayload(regionId))
        );
    }
}
