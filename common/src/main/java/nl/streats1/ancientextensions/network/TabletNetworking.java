package nl.streats1.ancientextensions.network;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.menu.JournalMenuOpener;
import nl.streats1.ancientextensions.menu.MigrationRouteChartMenuOpener;
import nl.streats1.ancientextensions.menu.PassportMenuOpener;
import nl.streats1.ancientextensions.menu.TabletMenuOpener;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class TabletNetworking {

    private TabletNetworking() {
    }

    public static void handle(ServerPlayer player, TabletActionPayload payload) {
        player.closeContainer();
        switch (payload.action()) {
            case TabletActionPayload.OPEN_JOURNAL -> JournalMenuOpener.open(player);
            case TabletActionPayload.OPEN_PASSPORT -> PassportMenuOpener.open(player);
            case TabletActionPayload.OPEN_CHART -> MigrationRouteChartMenuOpener.open(player);
            case TabletActionPayload.CLAIM_REWARDS -> {
                if (AncientExtensionsContext.get().tierRewards().claim(player, Optional.empty())) {
                    TabletMenuOpener.open(player);
                }
            }
            default -> {
            }
        }
    }
}
