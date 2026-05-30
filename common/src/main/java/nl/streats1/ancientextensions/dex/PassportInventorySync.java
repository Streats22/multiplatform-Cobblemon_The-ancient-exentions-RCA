package nl.streats1.ancientextensions.dex;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Writes the chosen origin onto every regional passport in the player's inventory.
 */
public final class PassportInventorySync {

    private PassportInventorySync() {
    }

    public static void applyOriginToPassports(ServerPlayer player, SurveyRegion region, SurveyOriginTown town) {
        Item passport = player.registryAccess()
                .registryOrThrow(Registries.ITEM)
                .get(AncientExtensionsConstants.id("regional_passport"));
        if (passport == null) {
            return;
        }
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(passport)) {
                PassportStackData.writeOrigin(stack, region, town);
            }
        }
    }
}
