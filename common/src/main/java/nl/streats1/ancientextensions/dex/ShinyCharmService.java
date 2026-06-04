package nl.streats1.ancientextensions.dex;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.config.ShinyCharmConfig;
import nl.streats1.ancientextensions.integration.cobblemon.CobblemonDexCompletion;
import nl.streats1.ancientextensions.registry.ModContent;

/**
 * Awards and applies the Regional Survey Shiny Charm bonus for full Cobblemon dex completion.
 */
public final class ShinyCharmService {

    private ShinyCharmService() {
    }

    public static CobblemonDexCompletion.Progress dexProgress(ServerPlayer player) {
        return CobblemonDexCompletion.progress(player);
    }

    public static boolean canClaim(ServerPlayer player) {
        if (!ShinyCharmConfig.enabled()) {
            return false;
        }
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        return !data.hasClaimedShinyCharm() && CobblemonDexCompletion.hasAllSpecies(player);
    }

    public static boolean hasActiveBonus(ServerPlayer player) {
        if (!ShinyCharmConfig.enabled()) {
            return false;
        }
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        if (!data.hasClaimedShinyCharm()) {
            return false;
        }
        if (!CobblemonDexCompletion.hasAllSpecies(player)) {
            return false;
        }
        if (ShinyCharmConfig.requireCharmInInventory() && !hasCharmInInventory(player)) {
            return false;
        }
        return true;
    }

    public static boolean claim(ServerPlayer player) {
        if (!ShinyCharmConfig.enabled()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.shiny_charm.disabled"));
            return false;
        }
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        if (data.hasClaimedShinyCharm()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.shiny_charm.already_claimed"));
            return false;
        }
        CobblemonDexCompletion.Progress progress = CobblemonDexCompletion.progress(player);
        if (!progress.complete()) {
            player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.shiny_charm.incomplete",
                    progress.owned(),
                    progress.total()
            ));
            return false;
        }

        data.markShinyCharmClaimed();
        AncientExtensionsContext.get().surveys().save(player, data);
        giveCharmItem(player);
        player.sendSystemMessage(Component.translatable("ancient_extensions.shiny_charm.claimed"));
        if (ShinyCharmConfig.requireCharmInInventory()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.shiny_charm.keep_in_bag"));
        } else {
            player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.shiny_charm.active_hint",
                    ShinyCharmConfig.rateMultiplier()
            ));
        }
        return true;
    }

    private static boolean hasCharmInInventory(ServerPlayer player) {
        if (ModContent.SHINY_CHARM == null) {
            return false;
        }
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.isEmpty() && stack.is(ModContent.SHINY_CHARM)) {
                return true;
            }
        }
        return false;
    }

    private static void giveCharmItem(ServerPlayer player) {
        if (ModContent.SHINY_CHARM == null) {
            return;
        }
        ItemStack charm = new ItemStack(ModContent.SHINY_CHARM);
        if (!player.getInventory().add(charm)) {
            player.drop(charm, false);
        }
    }
}
