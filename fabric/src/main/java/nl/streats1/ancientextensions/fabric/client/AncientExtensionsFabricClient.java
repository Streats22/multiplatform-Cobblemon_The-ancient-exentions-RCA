package nl.streats1.ancientextensions.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import nl.streats1.ancientextensions.client.*;
import nl.streats1.ancientextensions.network.ClaimTierRewardPayload;
import nl.streats1.ancientextensions.network.SelectSurveyRegionPayload;
import nl.streats1.ancientextensions.network.TabletActionPayload;
import nl.streats1.ancientextensions.registry.ModContent;

public class AncientExtensionsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playC2S().register(SelectSurveyRegionPayload.TYPE, SelectSurveyRegionPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ClaimTierRewardPayload.TYPE, ClaimTierRewardPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TabletActionPayload.TYPE, TabletActionPayload.STREAM_CODEC);
        AncientExtensionsScreens.register(MenuScreens::register);
        PokeballPouchClient.registerItemProperties();
        MigrationRouteCompassClient.registerItemProperties();
        if (ModContent.FIELD_SURVEY_MONITOR_BE != null) {
            BlockEntityRenderers.register(
                    ModContent.FIELD_SURVEY_MONITOR_BE,
                    FieldSurveyMonitorRenderer::new
            );
        }
        AncientExtensionsClientHooks.setOriginSelectSender((regionId, townId) ->
                ClientPlayNetworking.send(new SelectSurveyRegionPayload(regionId, townId))
        );
        AncientExtensionsClientHooks.setTierRewardClaimSender(() ->
                ClientPlayNetworking.send(new ClaimTierRewardPayload(""))
        );
        AncientExtensionsClientHooks.setTabletActionSender(action ->
                ClientPlayNetworking.send(new TabletActionPayload(action))
        );
    }
}
