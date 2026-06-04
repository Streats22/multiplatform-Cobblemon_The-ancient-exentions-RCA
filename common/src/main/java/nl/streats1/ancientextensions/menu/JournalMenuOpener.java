package nl.streats1.ancientextensions.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import nl.streats1.ancientextensions.menu.sync.JournalOpenData;

public final class JournalMenuOpener {

    private JournalMenuOpener() {
    }

    public static void open(ServerPlayer player) {
        JournalOpenData sync = RegionalSurveyJournalMenu.buildOpenData(player);
        MenuOpenHelper.open(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("item.ancient_extensions.regional_survey_journal");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player ignored) {
                return RegionalSurveyJournalMenu.forPlayer(containerId, inventory, player);
            }
        }, sync, buf -> JournalOpenData.STREAM_CODEC.encode(buf, sync));
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
    }
}
