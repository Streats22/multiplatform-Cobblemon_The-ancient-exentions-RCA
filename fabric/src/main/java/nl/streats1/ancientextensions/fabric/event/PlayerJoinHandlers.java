package nl.streats1.ancientextensions.fabric.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.display.RegionPlayerDisplay;
import nl.streats1.ancientextensions.integration.mca.McaIntegration;
import nl.streats1.ancientextensions.kit.StarterKitGrant;

public final class PlayerJoinHandlers {

    private PlayerJoinHandlers() {
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> server.execute(() -> {
            ServerPlayer player = handler.getPlayer();
            StarterKitGrant.tryGrantOnFirstJoin(player);
            RegionPlayerDisplay.refresh(player);
            AncientExtensionsContext.get().promptOriginIfNeeded(player);
            McaIntegration.schedulePassportPromptAfterMcaIntro(server, player);
        }));
    }
}
