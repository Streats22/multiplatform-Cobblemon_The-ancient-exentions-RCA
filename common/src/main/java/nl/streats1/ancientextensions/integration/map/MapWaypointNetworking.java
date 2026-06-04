package nl.streats1.ancientextensions.integration.map;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import nl.streats1.ancientextensions.network.MigrationWaypointPayload;

/**
 * Platform hook for sending migration waypoint packets to the client.
 */
public final class MapWaypointNetworking {

    @FunctionalInterface
    public interface Sender {
        void send(ServerPlayer player, MigrationWaypointPayload payload);
    }

    private static Sender sender = (player, payload) -> {
    };

    private MapWaypointNetworking() {
    }

    public static void setSender(@Nullable Sender value) {
        sender = value != null ? value : (player, payload) -> {
        };
    }

    public static void sendCreateWaypoint(
            ServerPlayer player,
            ResourceKey<Level> dimension,
            BlockPos pos,
            String label
    ) {
        ResourceLocation dimensionId = dimension.location();
        sender.send(
                player,
                new MigrationWaypointPayload(dimensionId, pos.getX(), pos.getY(), pos.getZ(), label)
        );
    }
}
