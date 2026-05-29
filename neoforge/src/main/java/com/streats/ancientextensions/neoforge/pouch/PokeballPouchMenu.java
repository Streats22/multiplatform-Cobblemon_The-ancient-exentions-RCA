package com.streats.ancientextensions.neoforge.pouch;

import com.streats.ancientextensions.neoforge.block.PokeballPouchBlockEntity;
import com.streats.ancientextensions.neoforge.registry.ModBlocks;
import com.streats.ancientextensions.neoforge.registry.ModItems;
import com.streats.ancientextensions.neoforge.registry.ModMenus;
import com.streats.ancientextensions.pouch.PokeballFilter;
import com.streats.ancientextensions.pouch.PokeballPouchConstants;
import com.streats.ancientextensions.pouch.PokeballPouchInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PokeballPouchMenu extends AbstractContainerMenu {

    public static final int WIDTH = 176;
    public static final int HEIGHT = 152;

    private static final int POUCH_ROWS = 2;
    private static final int POUCH_COLS = 9;
    private static final int POUCH_START_X = 8;
    private static final int POUCH_START_Y = 30;
    private static final int PLAYER_INV_START_Y = 86;
    private static final int HOTBAR_Y = 140;

    private final Container pouch;
    private final InteractionHand hand;
    private final ItemStack pouchStack;
    private final BlockPos blockPos;
    private final boolean blockMenu;

    public PokeballPouchMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, extraData.readBoolean(), extraData);
    }

    private PokeballPouchMenu(int containerId, Inventory playerInventory, boolean blockMenu, RegistryFriendlyByteBuf extraData) {
        super(ModMenus.POKEBALL_POUCH.get(), containerId);
        this.blockMenu = blockMenu;
        if (blockMenu) {
            this.blockPos = extraData.readBlockPos();
            this.hand = InteractionHand.MAIN_HAND;
            this.pouchStack = ItemStack.EMPTY;
            BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
            if (blockEntity instanceof PokeballPouchBlockEntity entity) {
                this.pouch = entity.container();
            } else {
                this.pouch = new PokeballPouchInventory(
                        net.minecraft.core.NonNullList.withSize(PokeballPouchConstants.SLOT_COUNT, ItemStack.EMPTY),
                        () -> {}
                );
            }
        } else {
            this.hand = extraData.readEnum(InteractionHand.class);
            this.pouchStack = playerInventory.player.getItemInHand(hand);
            this.blockPos = BlockPos.ZERO;
            this.pouch = PokeballPouchInventory.forItemStack(pouchStack);
        }
        addPouchAndPlayerSlots(playerInventory);
    }

    public static PokeballPouchMenu forItem(int containerId, Inventory playerInventory, ItemStack pouchStack, InteractionHand hand) {
        return new PokeballPouchMenu(containerId, playerInventory, pouchStack, hand);
    }

    public static PokeballPouchMenu forBlock(int containerId, Inventory playerInventory, BlockPos pos) {
        return new PokeballPouchMenu(containerId, playerInventory, pos);
    }

    private PokeballPouchMenu(int containerId, Inventory playerInventory, ItemStack pouchStack, InteractionHand hand) {
        super(ModMenus.POKEBALL_POUCH.get(), containerId);
        this.blockMenu = false;
        this.hand = hand;
        this.pouchStack = pouchStack;
        this.blockPos = BlockPos.ZERO;
        this.pouch = PokeballPouchInventory.forItemStack(pouchStack);
        addPouchAndPlayerSlots(playerInventory);
    }

    private PokeballPouchMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        super(ModMenus.POKEBALL_POUCH.get(), containerId);
        this.blockMenu = true;
        this.blockPos = pos;
        this.hand = InteractionHand.MAIN_HAND;
        this.pouchStack = ItemStack.EMPTY;
        Level level = playerInventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PokeballPouchBlockEntity entity) {
            this.pouch = entity.container();
        } else {
            this.pouch = new PokeballPouchInventory(
                    net.minecraft.core.NonNullList.withSize(PokeballPouchConstants.SLOT_COUNT, ItemStack.EMPTY),
                    () -> {}
            );
        }
        addPouchAndPlayerSlots(playerInventory);
    }

    private void addPouchAndPlayerSlots(Inventory playerInventory) {
        for (int row = 0; row < POUCH_ROWS; row++) {
            for (int col = 0; col < POUCH_COLS; col++) {
                int index = col + row * POUCH_COLS;
                this.addSlot(new PokeballSlot(
                        pouch,
                        index,
                        POUCH_START_X + col * 18,
                        POUCH_START_Y + row * 18
                ));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        POUCH_START_X + col * 18,
                        PLAYER_INV_START_Y + row * 18
                ));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, POUCH_START_X + col * 18, HOTBAR_Y));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        result = stack.copy();
        int pouchSlots = PokeballPouchConstants.SLOT_COUNT;

        if (index < pouchSlots) {
            if (!this.moveItemStackTo(stack, pouchSlots, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (PokeballFilter.isPokeball(stack) && !stack.is(ModItems.POKEBALL_POUCH.get()) && !stack.is(ModBlocks.POKEBALL_POUCH.get().asItem())) {
            if (!this.moveItemStackTo(stack, 0, pouchSlots, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == result.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockMenu) {
            return stillValidBlock(player, blockPos);
        }
        ItemStack held = player.getItemInHand(hand);
        return !held.isEmpty() && ItemStack.isSameItemSameComponents(held, pouchStack);
    }

    private static boolean stillValidBlock(Player player, BlockPos pos) {
        if (!(player.level().getBlockEntity(pos) instanceof PokeballPouchBlockEntity)) {
            return false;
        }
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!blockMenu && pouch instanceof PokeballPouchInventory inventory) {
            inventory.setChanged();
        }
    }
}
