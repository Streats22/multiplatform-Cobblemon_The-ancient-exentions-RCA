package nl.streats1.ancientextensions.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Shared extended tooltips so players know what each survey item does.
 */
public final class ItemGuideTooltips {

    private ItemGuideTooltips() {
    }

    public static void append(List<Component> tooltip, TooltipFlag flag, String... detailKeys) {
        for (String key : detailKeys) {
            tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
        if (flag.isAdvanced()) {
            tooltip.add(Component.translatable("ancient_extensions.guide.advanced_hint")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }
}
