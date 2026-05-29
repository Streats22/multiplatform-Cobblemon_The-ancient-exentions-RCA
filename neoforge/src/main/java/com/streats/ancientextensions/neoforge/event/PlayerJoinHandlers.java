package com.streats.ancientextensions.neoforge.event;

import com.streats.ancientextensions.kit.StarterKitGrant;
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
        player.server.execute(() -> StarterKitGrant.tryGrantOnFirstJoin(player));
    }
}
