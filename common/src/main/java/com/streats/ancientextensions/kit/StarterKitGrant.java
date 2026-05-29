package com.streats.ancientextensions.kit;

import com.streats.ancientextensions.AncientExtensionsConstants;
import com.streats.ancientextensions.dex.RegionalSurveyData;
import com.streats.ancientextensions.dex.RegionalSurveyService;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Grants the field kit once per player per world (not craftable).
 */
public final class StarterKitGrant {

    private StarterKitGrant() {
    }

    public static void tryGrantOnFirstJoin(ServerPlayer player) {
        grantJournalIfMissing(player);

        RegionalSurveyData data = RegionalSurveyService.get(player);
        if (data.hasStarterKitGranted() || data.hasDeployedProfessorsKit()) {
            return;
        }

        Item kitItem = player.registryAccess()
                .registryOrThrow(Registries.ITEM)
                .get(AncientExtensionsConstants.id("ancient_professors_kit"));
        if (kitItem == null) {
            return;
        }

        if (hasKitInInventory(player, kitItem)) {
            data.markStarterKitGranted();
            RegionalSurveyService.save(player, data);
            return;
        }

        ItemStack stack = new ItemStack(kitItem);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }

        data.markStarterKitGranted();
        RegionalSurveyService.save(player, data);

        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.welcome"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.welcome_hint"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.journal.welcome"));
    }

    public static void grantJournalIfMissing(ServerPlayer player) {
        Item journalItem = player.registryAccess()
                .registryOrThrow(Registries.ITEM)
                .get(AncientExtensionsConstants.id("regional_survey_journal"));
        if (journalItem == null || hasItemInInventory(player, journalItem)) {
            return;
        }
        ItemStack journal = new ItemStack(journalItem);
        if (!player.getInventory().add(journal)) {
            player.drop(journal, false);
        }
    }

    private static boolean hasItemInInventory(ServerPlayer player, Item item) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (player.getInventory().getItem(slot).is(item)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasKitInInventory(ServerPlayer player, Item kitItem) {
        return hasItemInInventory(player, kitItem);
    }
}
