package nl.streats1.ancientextensions.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Opens a dynamic written book without leaving a temporary item in the player's hand.
 */
public final class BookGuiHelper {

    private BookGuiHelper() {
    }

    public static void open(ServerPlayer player, InteractionHand hand, ItemStack book) {
        ItemStack held = player.getItemInHand(hand).copy();
        player.setItemInHand(hand, book);
        player.openItemGui(book, hand);
        // Restore on the next tick so the client still sees a written book in hand when the GUI opens.
        player.server.execute(() -> player.server.execute(() -> player.setItemInHand(hand, held)));
    }
}
