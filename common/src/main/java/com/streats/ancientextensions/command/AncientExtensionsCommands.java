package com.streats.ancientextensions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.streats.ancientextensions.dex.RegionalSurveyData;
import com.streats.ancientextensions.dex.RegionalSurveyService;
import com.streats.ancientextensions.AncientExtensionsConstants;
import com.streats.ancientextensions.kit.ProfessorsKitLogic;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import com.streats.ancientextensions.migration.MigrationConfig;
import com.streats.ancientextensions.migration.MigrationRoutes;
import com.streats.ancientextensions.migration.MigrationSeason;
import com.streats.ancientextensions.migration.MigrationSeasonClock;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public final class AncientExtensionsCommands {

    private AncientExtensionsCommands() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext context,
            Commands.CommandSelection environment
    ) {
        LiteralArgumentBuilder<CommandSourceStack> root = literal("ancientextensions")
                .then(literal("survey").executes(ctx -> showSurvey(ctx.getSource().getPlayerOrException())))
                .then(literal("migration").executes(ctx -> showMigration(ctx.getSource().getPlayerOrException())))
                .then(literal("deploykit").executes(ctx -> deployKit(ctx.getSource().getPlayerOrException())))
                .then(literal("deploykit")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> deployKit(EntityArgument.getPlayer(ctx, "player")))))
                .then(literal("givekit")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> giveKit(ctx.getSource().getPlayerOrException())))
                .then(literal("givekit")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> giveKitItem(EntityArgument.getPlayer(ctx, "player")))))
                .then(literal("givejournal")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> giveJournal(ctx.getSource().getPlayerOrException())))
                .then(literal("givejournal")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> giveJournal(EntityArgument.getPlayer(ctx, "player")))));

        dispatcher.register(root);
    }

    private static int showSurvey(ServerPlayer player) {
        RegionalSurveyData data = RegionalSurveyService.get(player);
        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.command.survey",
                data.getCaughtSpeciesCount(),
                data.getResearchPoints(),
                data.getTier().displayName(),
                data.hasDeployedProfessorsKit()
        ));
        return 1;
    }

    private static int showMigration(ServerPlayer player) {
        MigrationSeason season = MigrationSeasonClock.currentSeason(player.serverLevel());
        var route = MigrationRoutes.routeFor(season);
        RegionalSurveyData data = RegionalSurveyService.get(player);
        int completions = data.getMigrationCompletions(season);
        int nextReward = MigrationConfig.routeCompletionReward(completions);

        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.command.migration",
                season.displayName(),
                data.getMigrationLegIndex() + 1,
                route.size(),
                data.getCurrentLegCatches(),
                completions,
                nextReward
        ));
        for (int i = 0; i < route.size(); i++) {
            var leg = route.get(i);
            String marker = i == data.getMigrationLegIndex() ? ">" : " ";
            player.sendSystemMessage(Component.literal(marker + " " + (i + 1) + ". " + leg.biomeLabel()
                    + " (" + leg.requiredCatches() + " migratory catches, +" + leg.bonusResearchPoints() + " RP)"));
        }
        return 1;
    }

    private static int deployKit(ServerPlayer player) {
        return ProfessorsKitLogic.tryDeployKit(player) ? 1 : 0;
    }

    private static int giveKit(ServerPlayer player) {
        return giveKitItem(player);
    }

    private static int giveJournal(ServerPlayer player) {
        var journal = player.registryAccess()
                .registryOrThrow(Registries.ITEM)
                .get(AncientExtensionsConstants.id("regional_survey_journal"));
        if (journal == null) {
            player.sendSystemMessage(Component.literal("Regional Survey Journal is not registered."));
            return 0;
        }
        ItemStack stack = new ItemStack(journal);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        player.sendSystemMessage(Component.translatable("ancient_extensions.journal.given"));
        return 1;
    }

    private static int giveKitItem(ServerPlayer player) {
        var item = player.registryAccess()
                .registryOrThrow(Registries.ITEM)
                .get(AncientExtensionsConstants.id("ancient_professors_kit"));
        if (item == null) {
            player.sendSystemMessage(Component.literal("Ancient Professor's kit is not registered."));
            return 0;
        }
        ItemStack stack = new ItemStack(item);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        player.sendSystemMessage(Component.translatable("ancient_extensions.kit.given"));
        return 1;
    }
}
