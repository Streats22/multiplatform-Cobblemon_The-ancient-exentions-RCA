package nl.streats1.ancientextensions.pouch;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * Reads and writes pouch storage on an item stack via {@link DataComponents#CONTAINER}.
 * Capacity always follows {@link PouchTierData}; trailing empty slots are re-expanded on load.
 */
public final class PokeballPouchContents {

    private PokeballPouchContents() {
    }

    public static NonNullList<ItemStack> load(ItemStack pouchStack) {
        int capacity = PouchTierData.getSlotCount(pouchStack);
        NonNullList<ItemStack> items = NonNullList.withSize(capacity, ItemStack.EMPTY);
        ItemContainerContents contents = pouchStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(items);
        return items;
    }

    public static void save(ItemStack pouchStack, NonNullList<ItemStack> items) {
        pouchStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }
}
