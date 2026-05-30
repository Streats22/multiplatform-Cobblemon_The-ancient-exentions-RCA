package nl.streats1.ancientextensions.kit;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.List;

/**
 * Briefing item for new surveyors — readable written book placed on the camp lectern.
 */
public final class SurveyFieldNotes {

    private SurveyFieldNotes() {
    }

    public static ItemStack create() {
        List<Filterable<Component>> pages = List.of(
                Filterable.passThrough(Component.literal(
                        "Regional Survey Briefing\n\n"
                                + "Ancient Professor\n\n"
                                + "Only wild catches count for the Survey—not PC storage.\n\n"
                                + "First goals:\n"
                                + "• Catch 3 different species\n"
                                + "• Right-click your Regional Survey Journal\n"
                                + "• /ancientextensions survey or migration\n\n"
                                + "Backup supplies are in your camp chest."
                ).withStyle(ChatFormatting.BLACK))
        );

        WrittenBookContent content = new WrittenBookContent(
                Filterable.passThrough("Regional Survey Briefing"),
                "Ancient Professor",
                0,
                pages,
                true
        );

        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
        book.set(DataComponents.CUSTOM_NAME, Component.literal("Regional Survey Briefing")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        return book;
    }
}
