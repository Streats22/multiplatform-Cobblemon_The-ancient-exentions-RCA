package nl.streats1.ancientextensions.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import nl.streats1.ancientextensions.menu.sync.TabletOpenData;

public final class TabletMenuOpener {

    private TabletMenuOpener() {
    }

    public static void open(ServerPlayer player) {
        TabletOpenData sync = FieldSurveyTabletMenu.buildOpenData(player);
        MenuOpenHelper.open(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("item.ancient_extensions.field_survey_tablet");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player ignored) {
                return FieldSurveyTabletMenu.forPlayer(containerId, inventory, player);
            }
        }, sync, buf -> TabletOpenData.STREAM_CODEC.encode(buf, sync));
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 0.9F, 1.1F);
    }
}
