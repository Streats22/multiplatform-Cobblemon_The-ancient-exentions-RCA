package nl.streats1.ancientextensions.menu;

import nl.streats1.ancientextensions.menu.sync.ChartOpenData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class MigrationRouteChartMenuOpener {

    private MigrationRouteChartMenuOpener() {
    }

    public static void open(ServerPlayer player) {
        ChartOpenData sync = MigrationRouteChartMenu.buildOpenData(player);
        MenuOpenHelper.open(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("item.ancient_extensions.migration_route_chart");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player ignored) {
                return MigrationRouteChartMenu.forPlayer(containerId, inventory, player);
            }
        }, sync, buf -> ChartOpenData.STREAM_CODEC.encode(buf, sync));
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
    }
}
