package nl.streats1.ancientextensions.fabric.network;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import nl.streats1.ancientextensions.menu.MenuOpenHelper;

public final class FabricMenuOpenHelper {

    private FabricMenuOpenHelper() {
    }

    public static void register() {
        MenuOpenHelper.setOpener(FabricMenuOpenHelper::open);
    }

    private static void open(
            ServerPlayer player,
            MenuProvider provider,
            Object syncData,
            MenuOpenHelper.ExtraDataWriter writer
    ) {
        player.openMenu(new ExtendedScreenHandlerFactory<>() {
            @Override
            public Object getScreenOpeningData(ServerPlayer player) {
                return syncData;
            }

            @Override
            public net.minecraft.network.chat.Component getDisplayName() {
                return provider.getDisplayName();
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                return provider.createMenu(syncId, inv, player);
            }
        });
    }
}
