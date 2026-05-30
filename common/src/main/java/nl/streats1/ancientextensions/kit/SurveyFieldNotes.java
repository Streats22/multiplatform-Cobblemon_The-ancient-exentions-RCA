package nl.streats1.ancientextensions.kit;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

/**
 * Briefing item for new surveyors (written book styling via name + lore until tablet item exists).
 */
public final class SurveyFieldNotes {

    private SurveyFieldNotes() {
    }

    public static ItemStack create() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.set(DataComponents.CUSTOM_NAME, Component.literal("Regional Survey Briefing")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        book.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal("Ancient Professor").withStyle(ChatFormatting.DARK_GRAY),
                Component.empty(),
                Component.literal("Only wild catches count for the Survey—not PC storage."),
                Component.empty(),
                Component.literal("First goals:").withStyle(ChatFormatting.YELLOW),
                Component.literal("• Catch 3 different species"),
                Component.literal("• Right-click your Regional Survey Journal"),
                Component.literal("• /ancientextensions survey or migration"),
                Component.empty(),
                Component.literal("Backup supplies are in your camp chest.")
                        .withStyle(ChatFormatting.GRAY)
        )));
        return book;
    }
}
