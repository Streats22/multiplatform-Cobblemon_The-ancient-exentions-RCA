package nl.streats1.ancientextensions.command;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.config.PassportConfig;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.dex.ResearchTier;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.integration.map.MapWaypointIntegration;
import nl.streats1.ancientextensions.kit.ProfessorsKitLogic;
import nl.streats1.ancientextensions.kit.StarterKitGrant;
import nl.streats1.ancientextensions.migration.*;

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
                .then(literal("route")
                        .then(literal("locate").executes(ctx -> locateRoute(ctx.getSource().getPlayerOrException()))))
                .then(literal("passport").executes(ctx -> openPassport(ctx.getSource().getPlayerOrException())))
                .then(literal("passport")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> openPassport(EntityArgument.getPlayer(ctx, "player")))))
                .then(literal("journal").executes(ctx -> openJournal(ctx.getSource().getPlayerOrException())))
                .then(literal("journal")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> openJournal(EntityArgument.getPlayer(ctx, "player")))))
                .then(literal("startjoin")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> simulateFirstJoin(ctx.getSource().getPlayerOrException())))
                .then(literal("startjoin")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> simulateFirstJoin(EntityArgument.getPlayer(ctx, "player")))))
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
                                .executes(ctx -> giveKit(EntityArgument.getPlayer(ctx, "player")))))
                .then(literal("givejournal")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> giveJournal(ctx.getSource().getPlayerOrException())))
                .then(literal("givejournal")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> giveJournal(EntityArgument.getPlayer(ctx, "player")))))
                .then(literal("region")
                        .executes(ctx -> showRegion(ctx.getSource().getPlayerOrException()))
                        .then(literal("set")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("region", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (SurveyRegion region : SurveyRegion.values()) {
                                                builder.suggest(region.getId());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> setRegion(
                                                ctx.getSource().getPlayerOrException(),
                                                StringArgumentType.getString(ctx, "region")
                                        )))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .requires(source -> source.hasPermission(2))
                                        .then(Commands.argument("region", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    for (SurveyRegion region : SurveyRegion.values()) {
                                                        builder.suggest(region.getId());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(ctx -> setRegion(
                                                        EntityArgument.getPlayer(ctx, "player"),
                                                        StringArgumentType.getString(ctx, "region")
                                                ))))))
                .then(literal("origin")
                        .then(literal("setup")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> enableOriginSetup(ctx.getSource().getPlayerOrException()))
                                .then(literal("off")
                                        .executes(ctx -> disableOriginSetup(ctx.getSource().getPlayerOrException()))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> disableOriginSetup(EntityArgument.getPlayer(ctx, "player")))))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> enableOriginSetup(EntityArgument.getPlayer(ctx, "player"))))))
                .then(literal("rewards")
                        .executes(ctx -> showRewards(ctx.getSource().getPlayerOrException()))
                        .then(literal("claim")
                                .executes(ctx -> claimRewards(ctx.getSource().getPlayerOrException(), null))
                                .then(Commands.argument("tier", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (nl.streats1.ancientextensions.dex.ResearchTier tier :
                                                    nl.streats1.ancientextensions.dex.ResearchTier.values()) {
                                                builder.suggest(tier.name());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> claimRewards(
                                                ctx.getSource().getPlayerOrException(),
                                                StringArgumentType.getString(ctx, "tier")
                                        )))))
                .then(literal("givepassport")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> givePassport(ctx.getSource().getPlayerOrException())))
                .then(literal("givepassport")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> givePassport(EntityArgument.getPlayer(ctx, "player")))));

        dispatcher.register(root);
    }

    private static int showSurvey(ServerPlayer player) {
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.command.survey",
                data.getCaughtSpeciesCount(),
                data.getResearchPoints(),
                data.getTier().displayName(),
                data.hasDeployedProfessorsKit()
        ));
        sendRegionLine(player, data);
        return 1;
    }

    private static int showRegion(ServerPlayer player) {
        sendRegionLine(player, AncientExtensionsContext.get().surveys().get(player));
        return 1;
    }

    private static void sendRegionLine(ServerPlayer player, RegionalSurveyData data) {
        Component origin = data.getSurveyOrigin()
                .map(SurveyRegion::labeledName)
                .orElse(Component.translatable("ancient_extensions.command.region_unset"));
        player.sendSystemMessage(Component.translatable("ancient_extensions.command.region", origin));
    }

    private static int setRegion(ServerPlayer player, String regionId) {
        return AncientExtensionsContext.get().origins().setOriginAdmin(player, regionId) ? 1 : 0;
    }

    private static int showMigration(ServerPlayer player) {
        MigrationSeason season = MigrationSeasonClock.currentSeason(player.serverLevel());
        var route = MigrationRoutes.routeFor(season);
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        int completions = data.getMigrationCompletions(season);
        int nextReward = MigrationConfig.routeCompletionReward(completions);

        MigrationCalendarSource calendar = MigrationSeasonClock.calendarSource();
        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.command.migration",
                season.displayName(),
                data.getMigrationLegIndex() + 1,
                route.size(),
                data.getCurrentLegCatches(),
                completions,
                nextReward
        ));
        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.command.migration_calendar",
                calendar.label()
        ));
        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.command.migration_worldgen",
                MigrationBiomeCatalog.activeWorldGenLabel()
        ));
        for (int i = 0; i < route.size(); i++) {
            var leg = route.get(i);
            String marker = i == data.getMigrationLegIndex() ? ">" : " ";
            player.sendSystemMessage(Component.literal(marker + " " + (i + 1) + ". " + leg.biomeLabel()
                    + " (" + leg.requiredCatches() + " migratory catches, +" + leg.bonusResearchPoints() + " RP)"));
        }
        return 1;
    }

    private static int locateRoute(ServerPlayer player) {
        MigrationRouteTarget target = MigrationBiomeLocator.resolveForPlayer(player);
        switch (target.state()) {
            case ON_ROUTE -> player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.compass.on_route",
                    target.biomeLabel(),
                    target.legDisplay(),
                    target.legCount(),
                    target.catchesOnLeg(),
                    target.catchesRequired()
            ));
            case SEEKING_BIOME -> {
                player.sendSystemMessage(Component.translatable(
                        "ancient_extensions.compass.seeking",
                        target.biomeLabel(),
                        target.distanceBlocks(),
                        target.bearingLabel(),
                        target.legDisplay(),
                        target.legCount()
                ));
                MapWaypointIntegration.offerWaypoint(
                        player,
                        target.position(),
                        "Migration L" + target.legDisplay()
                );
            }
            case ROUTE_COMPLETE -> player.sendSystemMessage(
                    Component.translatable("ancient_extensions.compass.route_complete"));
            case NOT_FOUND -> player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.compass.not_found",
                    target.legDisplay(),
                    target.legCount()));
        }
        return 1;
    }

    private static int deployKit(ServerPlayer player) {
        return ProfessorsKitLogic.tryDeployKit(player) ? 1 : 0;
    }

    private static int openPassport(ServerPlayer player) {
        AncientExtensionsContext.get().openPassport(player);
        return 1;
    }

    private static int openJournal(ServerPlayer player) {
        AncientExtensionsContext.get().openJournal(player);
        return 1;
    }

    /**
     * Re-runs the first-join flow: starter items + passport if origin is not set yet.
     */
    private static int simulateFirstJoin(ServerPlayer player) {
        StarterKitGrant.tryGrantOnFirstJoin(player);
        var data = AncientExtensionsContext.get().surveys().get(player);
        if (!AncientExtensionsContext.get().origins().hasOrigin(data)) {
            if (PassportConfig.openOriginPickerOnJoin()) {
                AncientExtensionsContext.get().openPassport(player);
            } else {
                player.sendSystemMessage(Component.translatable("ancient_extensions.command.startjoin_origin_picker_disabled"));
            }
        } else {
            player.sendSystemMessage(Component.translatable("ancient_extensions.command.startjoin_already_stamped"));
        }
        return 1;
    }

    private static int enableOriginSetup(ServerPlayer player) {
        AncientExtensionsContext.get().origins().enableOriginSetup(player);
        player.sendSystemMessage(Component.translatable("ancient_extensions.command.origin_setup_enabled"));
        AncientExtensionsContext.get().openPassport(player);
        return 1;
    }

    private static int disableOriginSetup(ServerPlayer player) {
        if (AncientExtensionsContext.get().origins().disableOriginSetup(player)) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.command.origin_setup_disabled"));
            return 1;
        }
        player.sendSystemMessage(Component.translatable("ancient_extensions.command.origin_setup_not_active"));
        return 0;
    }

    private static int showRewards(ServerPlayer player) {
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        int pending = AncientExtensionsContext.get().tierRewards().unclaimedCount(data);
        player.sendSystemMessage(Component.translatable(
                "ancient_extensions.command.rewards_status",
                data.getTier().displayName(),
                pending
        ));
        for (ResearchTier tier : ResearchTier.values()) {
            boolean reached = data.getTier().ordinal() >= tier.ordinal();
            boolean claimed = data.hasClaimedTierReward(tier);
            if (claimed) {
                player.sendSystemMessage(Component.translatable(
                        "ancient_extensions.command.rewards_line_claimed",
                        tier.displayName()
                ));
            } else if (reached) {
                player.sendSystemMessage(Component.translatable(
                        "ancient_extensions.command.rewards_line_ready",
                        tier.displayName()
                ));
            } else {
                player.sendSystemMessage(Component.translatable(
                        "ancient_extensions.command.rewards_line_locked",
                        tier.displayName(),
                        tier.minPoints()
                ));
            }
        }
        if (pending > 0) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.command.rewards_claim_hint"));
        }
        return 1;
    }

    private static int claimRewards(ServerPlayer player, String tierName) {
        Optional<ResearchTier> tier = Optional.empty();
        if (tierName != null && !tierName.isBlank()) {
            try {
                tier = Optional.of(ResearchTier.valueOf(tierName.toUpperCase()));
            } catch (IllegalArgumentException exception) {
                player.sendSystemMessage(Component.translatable("ancient_extensions.rewards.invalid_tier"));
                return 0;
            }
        }
        return AncientExtensionsContext.get().tierRewards().claim(player, tier) ? 1 : 0;
    }

    private static int giveKit(ServerPlayer player) {
        return giveKitItem(player);
    }

    private static int giveJournal(ServerPlayer player) {
        return CommandItemHelper.giveItem(
                player,
                AncientExtensionsConstants.id("regional_survey_journal"),
                Component.translatable("ancient_extensions.journal.given"),
                Component.literal("Regional Survey Journal is not registered.")
        );
    }

    private static int givePassport(ServerPlayer player) {
        return CommandItemHelper.giveItem(
                player,
                AncientExtensionsConstants.id("regional_passport"),
                Component.translatable("ancient_extensions.passport.given"),
                Component.literal("Regional Passport is not registered.")
        );
    }

    private static int giveKitItem(ServerPlayer player) {
        return CommandItemHelper.giveItem(
                player,
                AncientExtensionsConstants.id("ancient_professors_kit"),
                Component.translatable("ancient_extensions.kit.given"),
                Component.literal("Ancient Professor's kit is not registered.")
        );
    }
}
