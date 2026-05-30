package nl.streats1.ancientextensions.block;

import nl.streats1.ancientextensions.menu.PokeballPouchMenu;
import nl.streats1.ancientextensions.registry.ModContent;
import nl.streats1.ancientextensions.pouch.PokeballPouchContents;
import nl.streats1.ancientextensions.pouch.PokeballPouchInventory;
import nl.streats1.ancientextensions.pouch.PouchTier;
import nl.streats1.ancientextensions.pouch.PouchTierData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    private PouchTier tier = PouchTier.POKE;
    private ResourceLocation ballId = PouchTierData.defaultBallId(PouchTier.POKE);
    private PokeballPouchInventory inventory = PokeballPouchInventory.create(PouchTier.POKE);

    public PokeballPouchBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.POKEBALL_POUCH_BE, pos, state);
    }

    public Container container() {
        return inventory;
    }

    public PouchTier tier() {
        return tier;
    }

    public void applyFromItemStack(ItemStack stack) {
        this.tier = PouchTierData.getTier(stack);
        this.ballId = PouchTierData.getBallId(stack);
        inventory.resize(tier, PokeballPouchContents.load(stack));
    }

    public void writeItemsToStack(ItemStack stack) {
        PouchTierData.writeFromStored(stack, tier, ballId);
        PokeballPouchContents.save(stack, inventory.items());
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
        tag.putString("Tier", tier.getId());
        tag.putString("BallId", ballId.toString());
        ItemStack scratch = new ItemStack(ModContent.POKEBALL_POUCH_BLOCK);
        PokeballPouchContents.save(scratch, inventory.items());
        tag.put("PouchData", scratch.save(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Tier")) {
            tier = PouchTier.fromName(tag.getString("Tier"));
            ballId = ResourceLocation.tryParse(tag.getString("BallId"));
            if (ballId == null) {
                ballId = PouchTierData.defaultBallId(tier);
            }
        }
        NonNullList<ItemStack> loaded = NonNullList.withSize(tier.slotCount(), ItemStack.EMPTY);
        if (tag.contains("PouchData")) {
            ItemStack scratch = ItemStack.parse(registries, tag.getCompound("PouchData")).orElse(ItemStack.EMPTY);
            NonNullList<ItemStack> parsed = PokeballPouchContents.load(scratch);
            for (int i = 0; i < Math.min(loaded.size(), parsed.size()); i++) {
                loaded.set(i, parsed.get(i));
            }
        }
        inventory = PokeballPouchInventory.create(tier);
        inventory.resize(tier, loaded);
    }
}
