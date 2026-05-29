package com.streats.ancientextensions.dex;

import com.streats.ancientextensions.migration.MigrationConfig;
import com.streats.ancientextensions.migration.MigrationLeg;
import com.streats.ancientextensions.migration.MigrationRoutes;
import com.streats.ancientextensions.migration.MigrationSeason;
import com.streats.ancientextensions.migration.MigrationSeasonClock;
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

/**
 * Builds the Regional Survey Journal as a readable written book and optional chat summary.
 */
public final class SurveyJournalReport {

    private static final int LINES_PER_PAGE = 12;

    private SurveyJournalReport() {
    }

    public static void openForPlayer(ServerPlayer player, InteractionHand hand) {
        RegionalSurveyData data = RegionalSurveyService.get(player);
        MigrationSeason season = MigrationSeasonClock.currentSeason(player.serverLevel());
        data.syncMigrationSeason(season);

        ItemStack book = createWrittenBook(player, data, season);
        player.openItemGui(book, hand);
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1.0f);
    }

    public static ItemStack createWrittenBook(ServerPlayer player, RegionalSurveyData data, MigrationSeason season) {
        List<Filterable<Component>> pages = buildPages(player, data, season);
        WrittenBookContent content = new WrittenBookContent(
                Filterable.passThrough(Component.translatable("ancient_extensions.journal.book_title").getString()),
                "Ancient Professor",
                0,
                pages,
                true
        );

        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
        book.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable("item.ancient_extensions.regional_survey_journal")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
        );
        return book;
    }

    private static List<Filterable<Component>> buildPages(
            ServerPlayer player,
            RegionalSurveyData data,
            MigrationSeason season
    ) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("ancient_extensions.journal.header")
                .withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD));
        lines.add(Component.empty());
        lines.add(Component.translatable(
                "ancient_extensions.journal.stats",
                data.getCaughtSpeciesCount(),
                data.getResearchPoints(),
                data.getTier().displayName()
        ).withStyle(ChatFormatting.BLACK));
        lines.add(Component.empty());
        lines.add(Component.translatable("ancient_extensions.journal.section_goals")
                .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD));

        for (SurveyGoal goal : SurveyGoals.build(data, season)) {
            lines.add(goal.statusLine());
        }

        lines.add(Component.empty());
        lines.add(Component.translatable("ancient_extensions.journal.section_migration")
                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD));
        lines.add(Component.translatable(
                "ancient_extensions.journal.migration_summary",
                season.displayName(),
                data.getMigrationLegIndex() + 1,
                MigrationRoutes.routeFor(season).size(),
                data.getMigrationCompletions(season),
                MigrationConfig.routeCompletionReward(data.getMigrationCompletions(season))
        ));

        var route = MigrationRoutes.routeFor(season);
        for (int i = 0; i < route.size(); i++) {
            MigrationLeg leg = route.get(i);
            ChatFormatting style = i < data.getMigrationLegIndex()
                    ? ChatFormatting.STRIKETHROUGH
                    : (i == data.getMigrationLegIndex() ? ChatFormatting.YELLOW : ChatFormatting.GRAY);
            String marker = i < data.getMigrationLegIndex() ? "[x]" : (i == data.getMigrationLegIndex() ? "[>]" : "[ ]");
            lines.add(Component.literal(marker + " " + leg.biomeLabel()
                            + " (" + leg.requiredCatches() + " catches)")
                    .withStyle(style));
        }

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
            pages.add(Filterable.passThrough(Component.translatable("ancient_extensions.journal.empty")));
        }
        return pages;
    }
}
