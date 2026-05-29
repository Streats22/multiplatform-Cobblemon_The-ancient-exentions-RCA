package com.streats.ancientextensions.neoforge.passport;

import com.streats.ancientextensions.dex.RegionalSurveyData;
import com.streats.ancientextensions.dex.RegionalSurveyService;
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
        RegionalSurveyData data = RegionalSurveyService.get(player);
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("item.ancient_extensions.regional_passport");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player ignored) {
                return RegionalPassportMenu.forPlayer(containerId, inventory, player);
            }
        }, buf -> RegionalPassportMenu.writeExtraData(buf, data, player));
    }
}
