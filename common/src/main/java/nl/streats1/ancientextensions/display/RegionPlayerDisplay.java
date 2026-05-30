package nl.streats1.ancientextensions.display;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.Optional;

/**
 * Shows each player's survey origin to others via scoreboard teams (tab list + name tag prefix).
 */
public final class RegionPlayerDisplay {

    private static final String TEAM_PREFIX = "ae_";

    private RegionPlayerDisplay() {
    }

    public static void refresh(ServerPlayer player) {
        refresh(player, false);
    }

    public static void refresh(ServerPlayer player, boolean announce) {
        Scoreboard scoreboard = player.server.getScoreboard();
        clearFromRegionTeams(scoreboard, player);

        Optional<SurveyRegion> origin = AncientExtensionsContext.get().surveys().get(player).getSurveyOrigin();
        if (origin.isEmpty()) {
            return;
        }

        SurveyRegion region = origin.get();
        PlayerTeam team = getOrCreateTeam(scoreboard, region);
        scoreboard.addPlayerToTeam(player.getScoreboardName(), team);

        if (announce) {
            player.server.getPlayerList().broadcastSystemMessage(
                    Component.translatable(
                            "ancient_extensions.origin.announce",
                            player.getDisplayName(),
                            region.labeledName()
                    ),
                    false
            );
        }
    }

    private static void clearFromRegionTeams(Scoreboard scoreboard, ServerPlayer player) {
        String playerName = player.getScoreboardName();
        for (SurveyRegion region : SurveyRegion.values()) {
            PlayerTeam team = scoreboard.getPlayerTeam(teamId(region));
            if (team != null && team.getPlayers().contains(playerName)) {
                scoreboard.removePlayerFromTeam(playerName, team);
            }
        }
    }

    private static PlayerTeam getOrCreateTeam(Scoreboard scoreboard, SurveyRegion region) {
        String teamName = teamId(region);
        PlayerTeam team = scoreboard.getPlayerTeam(teamName);
        if (team == null) {
            team = scoreboard.addPlayerTeam(teamName);
        }
        team.setColor(region.nameColor());
        team.setDisplayName(region.displayName());
        team.setPlayerPrefix(region.listBadge());
        team.setAllowFriendlyFire(true);
        team.setSeeFriendlyInvisibles(false);
        return team;
    }

    private static String teamId(SurveyRegion region) {
        return TEAM_PREFIX + region.getId();
    }
}
