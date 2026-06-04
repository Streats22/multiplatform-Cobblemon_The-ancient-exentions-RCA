package nl.streats1.ancientextensions.kit;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.dex.PassportInventorySync;
import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.config.PassportConfig;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
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
        grantTabletIfMissing(player);
        grantPassportIfMissing(player);

        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
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
            AncientExtensionsContext.get().surveys().save(player, data);
            return;
        }

        ItemStack stack = new ItemStack(kitItem);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }

        data.markStarterKitGranted();
        AncientExtensionsContext.get().surveys().save(player, data);

        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.welcome"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.welcome_hint"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.journal.welcome"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.tablet.welcome"));
        if (PassportConfig.openOriginPickerOnJoin()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.passport.welcome"));
        } else {
            player.sendSystemMessage(Component.translatable("ancient_extensions.passport.welcome_optional"));
        }
    }

    public static void grantPassportIfMissing(ServerPlayer player) {
        Item passportItem = player.registryAccess()
                .registryOrThrow(Registries.ITEM)
                .get(AncientExtensionsConstants.id("regional_passport"));
        if (passportItem == null || hasItemInInventory(player, passportItem)) {
            return;
        }
        ItemStack passport = new ItemStack(passportItem);
        if (!player.getInventory().add(passport)) {
            player.drop(passport, false);
        }
        var surveyData = AncientExtensionsContext.get().surveys().get(player);
        surveyData.getSurveyOrigin().ifPresent(region ->
                PassportInventorySync.applyOriginToPassports(
                        player,
                        region,
                        surveyData.getSurveyOriginTown().orElse(null)
                )
        );
    }

    public static void grantTabletIfMissing(ServerPlayer player) {
        Item tabletItem = player.registryAccess()
                .registryOrThrow(Registries.ITEM)
                .get(AncientExtensionsConstants.id("field_survey_tablet"));
        if (tabletItem == null || hasItemInInventory(player, tabletItem)) {
            return;
        }
        ItemStack tablet = new ItemStack(tabletItem);
        if (!player.getInventory().add(tablet)) {
            player.drop(tablet, false);
        }
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
