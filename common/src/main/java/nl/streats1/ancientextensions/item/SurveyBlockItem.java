package nl.streats1.ancientextensions.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

import nl.streats1.ancientextensions.util.ItemGuideTooltips;

/**
 * Block item with the same structured survey tooltip layout as handheld tools.
 */
public class SurveyBlockItem extends BlockItem {

    private final String roleKey;
    private final String summaryKey;
    private final String actionKey;
    private final String[] detailKeys;

    public SurveyBlockItem(
            Block block,
            Properties properties,
            String roleKey,
            String summaryKey,
            String actionKey,
            String... detailKeys
    ) {
        super(block, properties);
        this.roleKey = roleKey;
        this.summaryKey = summaryKey;
        this.actionKey = actionKey;
        this.detailKeys = detailKeys;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ItemGuideTooltips.appendSurveyItem(tooltip, flag, roleKey, summaryKey, detailKeys, actionKey);
    }
}
