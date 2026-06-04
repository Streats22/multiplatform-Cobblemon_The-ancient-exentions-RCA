package nl.streats1.ancientextensions.integration.map;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import nl.streats1.ancientextensions.util.ModPresence;

/**
 * Optional map mod hooks. JourneyMap reads special bracket text in chat; Xaero has no stable public API.
 */
public final class MapWaypointIntegration {

    public static final String JOURNEYMAP_MOD_ID = "journeymap";
    public static final String XAERO_MINIMAP_MOD_ID = "xaerominimap";
    public static final String XAERO_WORLD_MAP_MOD_ID = "xaeroworldmap";

    private MapWaypointIntegration() {
    }

    /**
     * Sends a system message players can click in JourneyMap to create a waypoint.
     * Always includes plain coordinates for other map mods / F3 users.
     */
    public static void offerWaypoint(ServerPlayer player, BlockPos pos, String label) {
        int y = pos.getY();
        MutableComponent coords = Component.translatable(
                "ancient_extensions.compass.waypoint_coords",
                label,
                pos.getX(),
                y,
                pos.getZ()
        );

        if (ModPresence.isLoaded(JOURNEYMAP_MOD_ID)) {
            String jmLink = String.format(
                    "[name:%s, x:%d, y:%d, z:%d]",
                    sanitize(label),
                    pos.getX(),
                    y,
                    pos.getZ()
            );
            player.sendSystemMessage(Component.literal(jmLink));
            player.sendSystemMessage(Component.translatable("ancient_extensions.compass.waypoint_jm_hint")
                    .withStyle(style -> style.withColor(0x55AAFF)));
        } else if (ModPresence.isLoaded(XAERO_MINIMAP_MOD_ID) || ModPresence.isLoaded(XAERO_WORLD_MAP_MOD_ID)) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.compass.waypoint_xaero_hint"));
        }

        player.sendSystemMessage(coords);
    }

    private static String sanitize(String label) {
        return label.replace('[', ' ').replace(']', ' ').trim();
    }
}
