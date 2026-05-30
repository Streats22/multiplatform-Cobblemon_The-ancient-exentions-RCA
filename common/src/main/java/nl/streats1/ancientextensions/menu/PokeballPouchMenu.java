package nl.streats1.ancientextensions.menu;

import nl.streats1.ancientextensions.block.PokeballPouchBlockEntity;
import nl.streats1.ancientextensions.registry.ModContent;
import nl.streats1.ancientextensions.menu.sync.PouchOpenData;
import nl.streats1.ancientextensions.registry.ModMenuTypes;
import nl.streats1.ancientextensions.pouch.PokeballFilter;
import nl.streats1.ancientextensions.pouch.PokeballPouchConstants;
import nl.streats1.ancientextensions.pouch.PokeballPouchLayout;
import nl.streats1.ancientextensions.pouch.PokeballPouchInventory;
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

    public static final int WIDTH = PokeballPouchLayout.WIDTH;

    private static final int POUCH_COLS = PokeballPouchConstants.COLS;
    private static final int POUCH_START_X = PokeballPouchLayout.POUCH_START_X;
    private static final int POUCH_START_Y = PokeballPouchLayout.POUCH_START_Y;

    private final Container pouch;
    private final InteractionHand hand;
    private final ItemStack pouchStack;
    private final BlockPos blockPos;
    private final boolean blockMenu;
    private final int pouchSlotCount;
    private final int imageHeight;

    public PokeballPouchMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, PouchOpenData.STREAM_CODEC.decode(extraData));
    }

    public PokeballPouchMenu(int containerId, Inventory playerInventory, PouchOpenData data) {
        this(containerId, playerInventory, data.blockMenu(), data);
    }

    private PokeballPouchMenu(int containerId, Inventory playerInventory, boolean blockMenu, PouchOpenData data) {
        super(ModMenuTypes.POKEBALL_POUCH, containerId);
        this.blockMenu = blockMenu;
        if (blockMenu) {
            this.blockPos = data.blockPos();
            this.hand = InteractionHand.MAIN_HAND;
            this.pouchStack = ItemStack.EMPTY;
            BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
            if (blockEntity instanceof PokeballPouchBlockEntity entity) {
                this.pouch = entity.container();
            } else {
                this.pouch = PokeballPouchInventory.create(nl.streats1.ancientextensions.pouch.PouchTier.POKE);
            }
        } else {
            this.hand = data.hand();
            this.pouchStack = playerInventory.player.getItemInHand(hand);
            this.blockPos = BlockPos.ZERO;
            this.pouch = PokeballPouchInventory.forItemStack(pouchStack);
        }
        this.pouchSlotCount = pouch.getContainerSize();
        this.imageHeight = PokeballPouchConstants.menuHeight(pouchSlotCount);
        addPouchAndPlayerSlots(playerInventory);
    }

    public static PokeballPouchMenu forItem(int containerId, Inventory playerInventory, ItemStack pouchStack, InteractionHand hand) {
        return new PokeballPouchMenu(containerId, playerInventory, pouchStack, hand);
    }

    public static PokeballPouchMenu forBlock(int containerId, Inventory playerInventory, BlockPos pos) {
        return new PokeballPouchMenu(containerId, playerInventory, pos);
    }

    private PokeballPouchMenu(int containerId, Inventory playerInventory, ItemStack pouchStack, InteractionHand hand) {
        super(ModMenuTypes.POKEBALL_POUCH, containerId);
        this.blockMenu = false;
        this.hand = hand;
        this.pouchStack = pouchStack;
        this.blockPos = BlockPos.ZERO;
        this.pouch = PokeballPouchInventory.forItemStack(pouchStack);
        this.pouchSlotCount = pouch.getContainerSize();
        this.imageHeight = PokeballPouchConstants.menuHeight(pouchSlotCount);
        addPouchAndPlayerSlots(playerInventory);
    }

    private PokeballPouchMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        super(ModMenuTypes.POKEBALL_POUCH, containerId);
        this.blockMenu = true;
        this.blockPos = pos;
        this.hand = InteractionHand.MAIN_HAND;
        this.pouchStack = ItemStack.EMPTY;
        Level level = playerInventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PokeballPouchBlockEntity entity) {
            this.pouch = entity.container();
        } else {
            this.pouch = PokeballPouchInventory.create(nl.streats1.ancientextensions.pouch.PouchTier.POKE);
        }
        this.pouchSlotCount = pouch.getContainerSize();
        this.imageHeight = PokeballPouchConstants.menuHeight(pouchSlotCount);
        addPouchAndPlayerSlots(playerInventory);
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getPouchSlotCount() {
        return pouchSlotCount;
    }

    private void addPouchAndPlayerSlots(Inventory playerInventory) {
        PokeballPouchLayout.Metrics layout = PokeballPouchLayout.metrics(pouchSlotCount);
        int pouchRows = layout.pouchRows();

        for (int row = 0; row < pouchRows; row++) {
            for (int col = 0; col < POUCH_COLS; col++) {
                int index = col + row * POUCH_COLS;
                if (index >= pouchSlotCount) {
                    break;
                }
                this.addSlot(new PokeballSlot(
                        pouch,
                        index,
                        POUCH_START_X + col * 18,
                        POUCH_START_Y + row * 18
                ));
            }
        }

        int playerInvStartY = layout.playerInvY();
        int hotbarY = layout.hotbarY();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        POUCH_START_X + col * 18,
                        playerInvStartY + row * 18
                ));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, POUCH_START_X + col * 18, hotbarY));
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

        if (index < pouchSlotCount) {
            if (!this.moveItemStackTo(stack, pouchSlotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (PokeballFilter.isPokeball(stack)
                && !stack.is(ModContent.POKEBALL_POUCH)
                && !stack.is(ModContent.POKEBALL_POUCH_BLOCK.asItem())) {
            if (!this.moveItemStackTo(stack, 0, pouchSlotCount, false)) {
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
