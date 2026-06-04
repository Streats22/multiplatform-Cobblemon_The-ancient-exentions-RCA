package nl.streats1.ancientextensions.item;

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

import nl.streats1.ancientextensions.menu.MigrationRouteChartMenuOpener;
import nl.streats1.ancientextensions.util.ItemGuideTooltips;

public class MigrationRouteChartItem extends Item {

    public MigrationRouteChartItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            MigrationRouteChartMenuOpener.open(serverPlayer);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ItemGuideTooltips.appendSurveyItem(
                tooltip,
                flag,
                "ancient_extensions.guide.role.chart",
                "item.ancient_extensions.migration_route_chart.description",
                new String[]{
                        "ancient_extensions.guide.chart_detail1",
                        "ancient_extensions.guide.chart_detail2",
                        "ancient_extensions.guide.chart_trade"
                },
                "ancient_extensions.migration_chart.tooltip_use"
        );
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }
}
