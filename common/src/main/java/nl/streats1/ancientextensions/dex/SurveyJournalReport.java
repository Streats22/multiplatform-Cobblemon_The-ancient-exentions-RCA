package nl.streats1.ancientextensions.dex;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.migration.MigrationConfig;
import nl.streats1.ancientextensions.migration.MigrationLeg;
import nl.streats1.ancientextensions.migration.MigrationRoutes;
import nl.streats1.ancientextensions.migration.MigrationSeason;
import nl.streats1.ancientextensions.migration.MigrationSeasonClock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.sounds.SoundEvents;
import nl.streats1.ancientextensions.util.BookGuiHelper;
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
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        MigrationSeason season = MigrationSeasonClock.currentSeason(player.serverLevel());
        data.syncMigrationSeason(season);

        ItemStack book = createWrittenBook(data, season);
        BookGuiHelper.open(player, hand, book);
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1.0f);
    }

    /** Live journal lines for the in-game field log screen. */
    public static List<Component> buildLines(RegionalSurveyData data, MigrationSeason season) {
        return buildContentLines(data, season);
    }

    public static ItemStack createWrittenBook(RegionalSurveyData data, MigrationSeason season) {
        List<Filterable<Component>> pages = buildPages(data, season);
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

    private static List<Filterable<Component>> buildPages(RegionalSurveyData data, MigrationSeason season) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("ancient_extensions.journal.header")
                .withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD));
        lines.add(Component.empty());
        lines.addAll(buildContentLines(data, season));
        return paginate(lines);
    }

    private static List<Component> buildContentLines(RegionalSurveyData data, MigrationSeason season) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(
                "ancient_extensions.journal.stats",
                data.getCaughtSpeciesCount(),
                data.getResearchPoints(),
                data.getTier().displayName()
        ).withStyle(ChatFormatting.DARK_GRAY));
        data.getSurveyOrigin().ifPresentOrElse(
                region -> {
                    Component originLine = region.labeledName();
                    lines.add(Component.translatable("ancient_extensions.journal.origin")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(" ")
                            .append(originLine));
                    data.getSurveyOriginTown().ifPresent(town -> lines.add(
                            Component.translatable("ancient_extensions.journal.hometown")
                                    .withStyle(ChatFormatting.DARK_GRAY)
                                    .append(" ")
                                    .append(town.displayName())
                    ));
                },
                () -> lines.add(Component.translatable("ancient_extensions.journal.origin_pending")
                        .withStyle(ChatFormatting.DARK_GRAY))
        );
        lines.add(Component.empty());
        lines.add(Component.translatable("ancient_extensions.journal.section_goals")
                .withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD));

        for (SurveyGoal goal : SurveyGoals.build(data, season)) {
            lines.add(journalGoalLine(goal));
        }

        lines.add(Component.empty());
        lines.add(Component.translatable("ancient_extensions.journal.section_migration")
                .withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD));
        lines.add(Component.translatable(
                "ancient_extensions.journal.migration_summary",
                season.displayName(),
                data.getMigrationLegIndex() + 1,
                MigrationRoutes.routeFor(season).size(),
                data.getMigrationCompletions(season),
                MigrationConfig.routeCompletionReward(data.getMigrationCompletions(season))
        ).withStyle(ChatFormatting.DARK_GRAY));

        var route = MigrationRoutes.routeFor(season);
        for (int i = 0; i < route.size(); i++) {
            MigrationLeg leg = route.get(i);
            ChatFormatting style = i < data.getMigrationLegIndex()
                    ? ChatFormatting.DARK_GRAY
                    : (i == data.getMigrationLegIndex() ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_GRAY);
            String marker = i < data.getMigrationLegIndex() ? "[x]" : (i == data.getMigrationLegIndex() ? "[>]" : "[ ]");
            MutableComponent line = Component.literal(marker + " ")
                    .append(leg.biomeLabelComponent())
                    .append(Component.literal(" (" + leg.requiredCatches() + " catches)"));
            if (i < data.getMigrationLegIndex()) {
                line.withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.DARK_GRAY);
            } else {
                line.withStyle(style);
            }
            lines.add(line);
        }

        return lines;
    }

    private static Component journalGoalLine(SurveyGoal goal) {
        Component line = goal.statusLine();
        if (goal.complete()) {
            return line.copy().withStyle(ChatFormatting.DARK_GRAY);
        }
        return line.copy().withStyle(ChatFormatting.BLACK);
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
