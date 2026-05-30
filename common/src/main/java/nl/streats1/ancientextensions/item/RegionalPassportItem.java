package nl.streats1.ancientextensions.item;

import nl.streats1.ancientextensions.dex.PassportStackData;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.AncientExtensionsContext;
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
        tooltip.add(Component.translatable("item.ancient_extensions.regional_passport.description")
                .withStyle(ChatFormatting.GRAY));
        Optional<SurveyRegion> fromStack = PassportStackData.readOrigin(stack);
        if (fromStack.isPresent()) {
            tooltip.add(Component.translatable("ancient_extensions.passport.tooltip_origin")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(fromStack.get().labeledName());
            PassportStackData.readOriginTown(stack).ifPresent(town ->
                    tooltip.add(Component.translatable("ancient_extensions.passport.tooltip_town", town.displayName())
                            .withStyle(ChatFormatting.GRAY))
            );
        } else {
            tooltip.add(Component.translatable("ancient_extensions.passport.tooltip_unregistered")
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
        tooltip.add(Component.translatable("ancient_extensions.passport.tooltip_use")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }
}
