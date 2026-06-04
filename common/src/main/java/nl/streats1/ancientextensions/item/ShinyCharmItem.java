package nl.streats1.ancientextensions.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

import nl.streats1.ancientextensions.util.ItemGuideTooltips;

public class ShinyCharmItem extends Item {

    public ShinyCharmItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ItemGuideTooltips.appendSurveyItem(
                tooltip,
                flag,
                "ancient_extensions.guide.role.shiny_charm",
                "item.ancient_extensions.shiny_charm.description",
                new String[]{
                        "ancient_extensions.guide.shiny_charm_detail1",
                        "ancient_extensions.guide.shiny_charm_detail2"
                },
                "ancient_extensions.shiny_charm.tooltip_keep"
        );
    }
}
