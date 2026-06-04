package nl.streats1.ancientextensions.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Shared extended tooltips so players know what each survey item does.
 */
public final class ItemGuideTooltips {

    private ItemGuideTooltips() {
    }

    public static void appendSurveyItem(
            List<Component> tooltip,
            TooltipFlag flag,
            String roleKey,
            String summaryKey,
            String[] detailKeys,
            String actionKey
    ) {
        appendRole(tooltip, roleKey);
        appendSummary(tooltip, summaryKey);
        appendDetails(tooltip, flag, detailKeys);
        appendAction(tooltip, actionKey);
    }

    public static void appendRole(List<Component> tooltip, String roleKey) {
        tooltip.add(Component.translatable(roleKey).withStyle(ChatFormatting.GOLD));
    }

    public static void appendSummary(List<Component> tooltip, String summaryKey) {
        tooltip.add(Component.translatable(summaryKey).withStyle(ChatFormatting.WHITE));
    }

    public static void appendDetails(List<Component> tooltip, TooltipFlag flag, String... detailKeys) {
        if (detailKeys.length == 0) {
            return;
        }
        tooltip.add(Component.empty());
        for (String key : detailKeys) {
            tooltip.add(bullet(key).withStyle(ChatFormatting.GRAY));
        }
        if (flag.isAdvanced()) {
            tooltip.add(Component.translatable("ancient_extensions.guide.advanced_hint")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    public static void appendAction(List<Component> tooltip, String actionKey) {
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable(actionKey).withStyle(ChatFormatting.YELLOW));
    }

    private static MutableComponent bullet(String translationKey) {
        return Component.literal("• ").append(Component.translatable(translationKey));
    }
}
