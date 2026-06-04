package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.client.integration.XaeroMinimapIntegration;
import nl.streats1.ancientextensions.network.MigrationWaypointPayload;

/**
 * Handles server-sent migration waypoint payloads on the client.
 */
public final class MigrationWaypointClient {

    private MigrationWaypointClient() {
    }

    public static void handle(MigrationWaypointPayload payload) {
        XaeroMinimapIntegration.tryCreateWaypoint(
                payload.dimension(),
                payload.x(),
                payload.y(),
                payload.z(),
                payload.label()
        );
    }
}
