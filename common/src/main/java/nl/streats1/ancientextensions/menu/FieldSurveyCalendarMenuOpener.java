package nl.streats1.ancientextensions.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import nl.streats1.ancientextensions.menu.sync.ChartOpenData;

public final class FieldSurveyCalendarMenuOpener {

    private FieldSurveyCalendarMenuOpener() {
    }

    public static void open(ServerPlayer player, BlockPos pos) {
        ChartOpenData sync = FieldSurveyCalendarMenu.buildOpenData(player.serverLevel(), pos);
        MenuOpenHelper.open(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("block.ancient_extensions.field_survey_calendar");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player ignored) {
                return FieldSurveyCalendarMenu.forBlock(containerId, inventory, player, pos);
            }
        }, sync, buf -> ChartOpenData.STREAM_CODEC.encode(buf, sync));
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 0.9F, 1.05F);
    }
}
