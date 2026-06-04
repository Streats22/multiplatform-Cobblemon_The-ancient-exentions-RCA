package nl.streats1.ancientextensions.item;

import nl.streats1.ancientextensions.menu.TabletMenuOpener;
import nl.streats1.ancientextensions.util.ItemGuideTooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

public class FieldSurveyTabletItem extends Item {

    public FieldSurveyTabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            TabletMenuOpener.open(serverPlayer);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.ancient_extensions.field_survey_tablet.description")
                .withStyle(ChatFormatting.GRAY));
        ItemGuideTooltips.append(
                tooltip,
                flag,
                "ancient_extensions.guide.tablet_detail1",
                "ancient_extensions.guide.tablet_detail2"
        );
        tooltip.add(Component.translatable("ancient_extensions.tablet.tooltip_use")
                .withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }
}
