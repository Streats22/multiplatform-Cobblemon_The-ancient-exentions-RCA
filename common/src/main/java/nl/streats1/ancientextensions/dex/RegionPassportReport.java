package nl.streats1.ancientextensions.dex;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.ArrayList;
import java.util.List;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.util.BookGuiHelper;

/**
 * Regional passport as a readable stamp book once origin is registered.
 */
public final class RegionPassportReport {

    private static final int LINES_PER_PAGE = 12;

    private RegionPassportReport() {
    }

    public static void openForPlayer(ServerPlayer player, InteractionHand hand) {
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        SurveyRegion region = data.getSurveyOrigin().orElse(null);
        if (region == null) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.passport.not_registered"));
            return;
        }

        ItemStack book = createWrittenBook(region, data);
        BookGuiHelper.open(player, hand, book);
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1.0f);
    }

    public static ItemStack createWrittenBook(SurveyRegion region, RegionalSurveyData data) {
        List<Filterable<Component>> pages = buildPages(region, data);
        WrittenBookContent content = new WrittenBookContent(
                Filterable.passThrough(Component.translatable("ancient_extensions.passport.book_title").getString()),
                "Regional Survey Office",
                0,
                pages,
                true
        );

        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
        book.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable("item.ancient_extensions.regional_passport")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
        );
        return book;
    }

    private static List<Filterable<Component>> buildPages(SurveyRegion region, RegionalSurveyData data) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("ancient_extensions.passport.header")
                .withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD));
        lines.add(Component.empty());
        lines.add(Component.translatable("ancient_extensions.passport.origin_label")
                .withStyle(ChatFormatting.DARK_GRAY));
        lines.add(region.displayName().copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        data.getSurveyOriginTown().ifPresent(town -> lines.add(
                Component.translatable("ancient_extensions.passport.town_label")
                        .withStyle(ChatFormatting.DARK_GRAY)
                        .append(" ")
                        .append(town.displayName().copy().withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD))
        ));
        lines.add(Component.empty());
        lines.add(region.passportBlurb().copy().withStyle(ChatFormatting.BLACK));
        lines.add(Component.empty());
        lines.add(Component.translatable(
                "ancient_extensions.passport.stats",
                data.getCaughtSpeciesCount(),
                data.getResearchPoints(),
                data.getTier().displayName()
        ).withStyle(ChatFormatting.DARK_GREEN));
        lines.add(Component.empty());
        lines.add(Component.translatable("ancient_extensions.passport.footer")
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));

        return paginate(lines);
    }

    private static List<Filterable<Component>> paginate(List<Component> lines) {
        List<Filterable<Component>> pages = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += LINES_PER_PAGE) {
            int end = Math.min(i + LINES_PER_PAGE, lines.size());
            MutableComponent page = Component.empty();
            for (int line = i; line < end; line++) {
                if (line > i) {
                    page.append("\n");
                }
                page.append(lines.get(line));
            }
            pages.add(Filterable.passThrough(page));
        }
        if (pages.isEmpty()) {
            pages.add(Filterable.passThrough(Component.translatable("ancient_extensions.passport.empty")));
        }
        return pages;
    }
}
