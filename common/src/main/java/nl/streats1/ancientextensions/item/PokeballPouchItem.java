package nl.streats1.ancientextensions.item;

import nl.streats1.ancientextensions.menu.MenuOpenHelper;
import nl.streats1.ancientextensions.menu.sync.PouchOpenData;
import nl.streats1.ancientextensions.menu.PokeballPouchMenu;
import nl.streats1.ancientextensions.pouch.PouchTier;
import nl.streats1.ancientextensions.pouch.PouchTierData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class PokeballPouchItem extends BlockItem {

    public PokeballPouchItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            PouchOpenData sync = PouchOpenData.forItem(hand);
            MenuOpenHelper.open(serverPlayer, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return stack.getHoverName();
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player ignored) {
                    return PokeballPouchMenu.forItem(containerId, inventory, stack, hand);
                }
            }, sync, buf -> PouchOpenData.STREAM_CODEC.encode(buf, sync));
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        PouchTier tier = PouchTierData.getTier(stack);
        if (tier == PouchTier.POKE) {
            return super.getName(stack);
        }
        return Component.translatable("item.ancient_extensions.pokeball_pouch.tier." + tier.getId());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        PouchTier tier = PouchTierData.getTier(stack);
        tooltip.add(Component.translatable("item.ancient_extensions.pokeball_pouch.description")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(
                "item.ancient_extensions.pokeball_pouch.slots",
                PouchTierData.getSlotCount(stack)
        ).withStyle(ChatFormatting.DARK_AQUA));
        if (tier != PouchTier.POKE) {
            tooltip.add(Component.translatable("item.ancient_extensions.pokeball_pouch.crafted_with")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable(PouchTierData.getBallId(stack).toLanguageKey())
                    .withStyle(ChatFormatting.GOLD));
        } else if (!PouchTierData.getBallId(stack).getPath().equals("poke_ball")) {
            tooltip.add(Component.translatable("item.ancient_extensions.pokeball_pouch.crafted_with")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable(PouchTierData.getBallId(stack).toLanguageKey())
                    .withStyle(ChatFormatting.GOLD));
        }
        tooltip.add(Component.translatable("item.ancient_extensions.pokeball_pouch.place_hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
