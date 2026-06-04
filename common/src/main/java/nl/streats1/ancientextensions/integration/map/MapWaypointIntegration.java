package nl.streats1.ancientextensions.integration.map;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import nl.streats1.ancientextensions.util.ModPresence;

/**
 * Optional map mod hooks for the Migration Route Compass.
 * Xaero's Minimap / World Map is preferred; JourneyMap uses a clickable chat fallback.
 */
public final class MapWaypointIntegration {

    public static final String JOURNEYMAP_MOD_ID = "journeymap";
    public static final String XAERO_MINIMAP_MOD_ID = "xaerominimap";
    public static final String XAERO_WORLD_MAP_MOD_ID = "xaeroworldmap";

    private MapWaypointIntegration() {
    }

    public static boolean hasXaero() {
        return ModPresence.isLoaded(XAERO_MINIMAP_MOD_ID) || ModPresence.isLoaded(XAERO_WORLD_MAP_MOD_ID);
    }

    public static boolean hasJourneyMap() {
        return ModPresence.isLoaded(JOURNEYMAP_MOD_ID);
    }

    /**
     * Creates a map waypoint when possible and always sends plain coordinates as backup.
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

        if (hasXaero()) {
            MapWaypointNetworking.sendCreateWaypoint(
                    player,
                    player.serverLevel().dimension(),
                    pos,
                    label
            );
            player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.compass.waypoint_xaero_created",
                    label
            ));
        } else if (hasJourneyMap()) {
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
        }

        player.sendSystemMessage(coords);
    }

    private static String sanitize(String label) {
        return label.replace('[', ' ').replace(']', ' ').trim();
    }
}
