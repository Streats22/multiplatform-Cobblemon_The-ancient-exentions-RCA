package nl.streats1.ancientextensions.menu;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.menu.sync.PassportOpenData;
import nl.streats1.ancientextensions.menu.MenuOpenHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class PassportMenuOpener {

    private PassportMenuOpener() {
    }

    public static void open(ServerPlayer player) {
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        PassportOpenData sync = PassportOpenData.from(data, player);
        MenuOpenHelper.open(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("item.ancient_extensions.regional_passport");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player ignored) {
                return RegionalPassportMenu.forPlayer(containerId, inventory, player);
            }
        }, sync, buf -> PassportOpenData.STREAM_CODEC.encode(buf, sync));
    }
}
