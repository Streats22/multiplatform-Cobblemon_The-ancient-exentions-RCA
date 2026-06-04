package nl.streats1.ancientextensions.item;

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
import java.util.Optional;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.dex.PassportStackData;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.util.ItemGuideTooltips;

public class RegionalPassportItem extends Item {

    public RegionalPassportItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            AncientExtensionsContext.get().openPassport(serverPlayer);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ItemGuideTooltips.appendRole(tooltip, "ancient_extensions.guide.role.passport");
        ItemGuideTooltips.appendSummary(tooltip, "item.ancient_extensions.regional_passport.description");
        ItemGuideTooltips.appendDetails(
                tooltip,
                flag,
                "ancient_extensions.guide.passport_detail1",
                "ancient_extensions.guide.passport_detail2",
                "ancient_extensions.guide.passport_craft"
        );
        Optional<SurveyRegion> fromStack = PassportStackData.readOrigin(stack);
        tooltip.add(Component.empty());
        if (fromStack.isPresent()) {
            tooltip.add(Component.translatable("ancient_extensions.passport.tooltip_origin")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(fromStack.get().labeledName().copy().withStyle(ChatFormatting.GREEN));
            PassportStackData.readOriginTown(stack).ifPresent(town ->
                    tooltip.add(Component.translatable("ancient_extensions.passport.tooltip_town", town.displayName())
                            .withStyle(ChatFormatting.GRAY))
            );
        } else {
            tooltip.add(Component.translatable("ancient_extensions.passport.tooltip_unregistered")
                    .withStyle(ChatFormatting.RED));
        }
        ItemGuideTooltips.appendAction(tooltip, "ancient_extensions.passport.tooltip_use");
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }
}
