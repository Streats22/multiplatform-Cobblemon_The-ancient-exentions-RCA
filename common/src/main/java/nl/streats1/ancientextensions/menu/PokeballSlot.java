package nl.streats1.ancientextensions.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import nl.streats1.ancientextensions.pouch.PokeballFilter;
import nl.streats1.ancientextensions.registry.ModContent;

public class PokeballSlot extends Slot {

    public PokeballSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return PokeballFilter.isPokeball(stack)
                && !stack.is(ModContent.POKEBALL_POUCH)
                && !stack.is(ModContent.POKEBALL_POUCH_BLOCK.asItem());
    }
}
