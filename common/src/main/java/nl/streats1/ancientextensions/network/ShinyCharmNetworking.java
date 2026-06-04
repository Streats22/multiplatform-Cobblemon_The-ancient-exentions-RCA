package nl.streats1.ancientextensions.network;

import net.minecraft.server.level.ServerPlayer;

import nl.streats1.ancientextensions.dex.ShinyCharmService;
import nl.streats1.ancientextensions.menu.PassportMenuOpener;

public final class ShinyCharmNetworking {

    private ShinyCharmNetworking() {
    }

    public static void handleClaim(ServerPlayer player) {
        if (ShinyCharmService.claim(player)) {
            PassportMenuOpener.open(player);
        }
    }
}
