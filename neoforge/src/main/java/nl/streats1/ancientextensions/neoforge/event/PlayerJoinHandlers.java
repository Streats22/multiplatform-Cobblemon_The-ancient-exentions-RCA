package nl.streats1.ancientextensions.neoforge.event;

import nl.streats1.ancientextensions.kit.StarterKitGrant;
import nl.streats1.ancientextensions.display.RegionPlayerDisplay;
import nl.streats1.ancientextensions.neoforge.network.ModNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public final class PlayerJoinHandlers {

    private PlayerJoinHandlers() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        player.server.execute(() -> {
            StarterKitGrant.tryGrantOnFirstJoin(player);
            RegionPlayerDisplay.refresh(player);
            ModNetworking.promptOriginIfNeeded(player);
        });
    }
}
