package nl.streats1.ancientextensions.migration;

import net.minecraft.core.BlockPos;

import org.jetbrains.annotations.Nullable;

/**
 * Result of locating the player's active migration leg in the world.
 */
public record MigrationRouteTarget(
        State state,
        @Nullable BlockPos position,
        String biomeLabel,
        int distanceBlocks,
        String bearingLabel,
        int legDisplay,
        int legCount,
        int catchesOnLeg,
        int catchesRequired
) {

    public enum State {
        /**
         * Player is in the correct leg biome; finish catches here.
         */
        ON_ROUTE,
        /**
         * Compass points toward a matching route biome.
         */
        SEEKING_BIOME,
        /**
         * All legs done this season.
         */
        ROUTE_COMPLETE,
        /**
         * No matching biome found within search range.
         */
        NOT_FOUND
    }

    public static MigrationRouteTarget onRoute(
            String biomeLabel,
            int legDisplay,
            int legCount,
            int catches,
            int required
    ) {
        return new MigrationRouteTarget(
                State.ON_ROUTE,
                null,
                biomeLabel,
                0,
                "",
                legDisplay,
                legCount,
                catches,
                required
        );
    }

    public static MigrationRouteTarget seeking(
            BlockPos position,
            String biomeLabel,
            int distanceBlocks,
            String bearingLabel,
            int legDisplay,
            int legCount
    ) {
        return new MigrationRouteTarget(
                State.SEEKING_BIOME,
                position,
                biomeLabel,
                distanceBlocks,
                bearingLabel,
                legDisplay,
                legCount,
                0,
                0
        );
    }

    public static MigrationRouteTarget routeComplete(int legCount) {
        return new MigrationRouteTarget(
                State.ROUTE_COMPLETE,
                null,
                "",
                0,
                "",
                legCount,
                legCount,
                0,
                0
        );
    }

    public static MigrationRouteTarget notFound(int legDisplay, int legCount) {
        return new MigrationRouteTarget(
                State.NOT_FOUND,
                null,
                "",
                0,
                "",
                legDisplay,
                legCount,
                0,
                0
        );
    }

    public boolean hasCompassTarget() {
        return state == State.SEEKING_BIOME && position != null;
    }
}
