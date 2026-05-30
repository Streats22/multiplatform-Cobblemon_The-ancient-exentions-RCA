package nl.streats1.ancientextensions.block;

import nl.streats1.ancientextensions.menu.PokeballPouchMenu;
import nl.streats1.ancientextensions.registry.ModContent;
import nl.streats1.ancientextensions.pouch.PokeballPouchConstants;
import nl.streats1.ancientextensions.pouch.PokeballPouchContents;
import nl.streats1.ancientextensions.pouch.PokeballPouchInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PokeballPouchBlockEntity extends BlockEntity implements MenuProvider {

    private final NonNullList<ItemStack> items = NonNullList.withSize(PokeballPouchConstants.SLOT_COUNT, ItemStack.EMPTY);
    private final PokeballPouchInventory inventory = new PokeballPouchInventory(items, this::setChanged);

    public PokeballPouchBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.POKEBALL_POUCH_BE, pos, state);
    }

    public Container container() {
        return inventory;
    }

    public void writeItemsToStack(ItemStack stack) {
        PokeballPouchContents.save(stack, items);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ancient_extensions.pokeball_pouch");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return PokeballPouchMenu.forBlock(containerId, playerInventory, worldPosition);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ItemStack scratch = new ItemStack(ModContent.POKEBALL_POUCH_BLOCK);
        PokeballPouchContents.save(scratch, items);
        tag.put("PouchData", scratch.save(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("PouchData")) {
            ItemStack scratch = ItemStack.parse(registries, tag.getCompound("PouchData")).orElse(ItemStack.EMPTY);
            NonNullList<ItemStack> loaded = PokeballPouchContents.load(scratch);
            for (int i = 0; i < items.size(); i++) {
                items.set(i, loaded.get(i));
            }
        }
    }

}
