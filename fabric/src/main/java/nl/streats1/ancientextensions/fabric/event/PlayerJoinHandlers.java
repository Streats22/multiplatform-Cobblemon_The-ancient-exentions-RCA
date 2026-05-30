package nl.streats1.ancientextensions.fabric.event;

import nl.streats1.ancientextensions.display.RegionPlayerDisplay;
import nl.streats1.ancientextensions.fabric.network.FabricNetworking;
import nl.streats1.ancientextensions.kit.StarterKitGrant;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerJoinHandlers {

    private PlayerJoinHandlers() {
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> server.execute(() -> {
            ServerPlayer player = handler.getPlayer();
            StarterKitGrant.tryGrantOnFirstJoin(player);
            RegionPlayerDisplay.refresh(player);
            FabricNetworking.promptOriginIfNeeded(player);
        }));
    }
}
