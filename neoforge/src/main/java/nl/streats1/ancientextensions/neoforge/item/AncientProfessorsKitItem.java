package nl.streats1.ancientextensions.neoforge.item;

import nl.streats1.ancientextensions.kit.ProfessorsKitLogic;
import nl.streats1.ancientextensions.neoforge.kit.KitAdvancements;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class AncientProfessorsKitItem extends Item {

    public AncientProfessorsKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.pass(stack);
        }

        if (ProfessorsKitLogic.tryDeployKit(serverPlayer)) {
            KitAdvancements.awardCampPitched(serverPlayer);
            stack.shrink(1);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.ancient_extensions.ancient_professors_kit.description")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("ancient_extensions.kit.tooltip_use")
                .withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }
}
