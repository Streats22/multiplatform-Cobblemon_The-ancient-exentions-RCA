package nl.streats1.ancientextensions.pouch;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Poké Ball-only inventory for item stacks and placed blocks. Size follows craft tier.
 */
public class PokeballPouchInventory implements Container {

    private NonNullList<ItemStack> items;
    private final Runnable onChanged;

    public PokeballPouchInventory(NonNullList<ItemStack> items, Runnable onChanged) {
        this.items = items;
        this.onChanged = onChanged;
    }

    public static PokeballPouchInventory forItemStack(ItemStack pouchStack) {
        NonNullList<ItemStack> items = PokeballPouchContents.load(pouchStack);
        return new PokeballPouchInventory(items, () -> PokeballPouchContents.save(pouchStack, items));
    }

    public static PokeballPouchInventory create(PouchTier tier) {
        return new PokeballPouchInventory(
                NonNullList.withSize(tier.slotCount(), ItemStack.EMPTY),
                () -> {}
        );
    }

    public void resize(PouchTier tier, NonNullList<ItemStack> existing) {
        NonNullList<ItemStack> resized = NonNullList.withSize(tier.slotCount(), ItemStack.EMPTY);
        for (int i = 0; i < Math.min(existing.size(), resized.size()); i++) {
            resized.set(i, existing.get(i));
        }
        this.items = resized;
        setChanged();
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    @Override
    public void setChanged() {
        onChanged.run();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return PokeballFilter.isPokeball(stack);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        setChanged();
    }

    public NonNullList<ItemStack> items() {
        return items;
    }
}
