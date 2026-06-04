package nl.streats1.ancientextensions.neoforge.client;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import nl.streats1.ancientextensions.client.*;
import nl.streats1.ancientextensions.network.ClaimTierRewardPayload;
import nl.streats1.ancientextensions.network.SelectSurveyRegionPayload;
import nl.streats1.ancientextensions.network.TabletActionPayload;
import nl.streats1.ancientextensions.registry.ModContent;

public final class AncientExtensionsNeoForgeClient {

    private AncientExtensionsNeoForgeClient() {
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        AncientExtensionsScreens.register(event::register);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (ModContent.FIELD_SURVEY_MONITOR_BE != null) {
            event.registerBlockEntityRenderer(
                    ModContent.FIELD_SURVEY_MONITOR_BE,
                    FieldSurveyMonitorRenderer::new
            );
        }
    }

    public static void initClientHooks() {
        PokeballPouchClient.registerItemProperties();
        MigrationRouteCompassClient.registerItemProperties();
        AncientExtensionsClientHooks.setOriginSelectSender((regionId, townId) ->
                PacketDistributor.sendToServer(new SelectSurveyRegionPayload(regionId, townId))
        );
        AncientExtensionsClientHooks.setTierRewardClaimSender(() ->
                PacketDistributor.sendToServer(new ClaimTierRewardPayload(""))
        );
        AncientExtensionsClientHooks.setTabletActionSender(action ->
                PacketDistributor.sendToServer(new TabletActionPayload(action))
        );
    }
}
