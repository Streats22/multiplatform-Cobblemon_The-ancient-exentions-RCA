package nl.streats1.ancientextensions.command;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CommandItemHelper {

    private CommandItemHelper() {
    }

    public static int giveItem(
            ServerPlayer player,
            ResourceLocation itemId,
            Component givenMessage,
            Component notRegisteredMessage
    ) {
        Item item = player.registryAccess()
                .registryOrThrow(Registries.ITEM)
                .get(itemId);
        if (item == null) {
            player.sendSystemMessage(notRegisteredMessage);
            return 0;
        }
        ItemStack stack = new ItemStack(item);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        player.sendSystemMessage(givenMessage);
        return 1;
    }
}
