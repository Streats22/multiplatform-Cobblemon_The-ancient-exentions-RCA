package nl.streats1.ancientextensions.client.integration;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import org.slf4j.Logger;

import nl.streats1.ancientextensions.integration.map.MapWaypointIntegration;
import nl.streats1.ancientextensions.integration.map.MapWaypointIntegration;

/**
 * Creates waypoints in Xaero's Minimap / World Map via reflection (optional mod — no compile dependency).
 */
public final class XaeroMinimapIntegration {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String MIGRATION_PREFIX = "Migration";

    private XaeroMinimapIntegration() {
    }

    public static boolean isAvailable() {
        return MapWaypointIntegration.hasXaero();
    }

    public static boolean tryCreateWaypoint(ResourceLocation dimension, int x, int y, int z, String label) {
        if (!isAvailable()) {
            return false;
        }
        try {
            return createWaypointReflect(dimension, x, y, z, sanitize(label));
        } catch (ReflectiveOperationException | RuntimeException exception) {
            LOGGER.warn("Failed to create Xaero migration waypoint", exception);
            return false;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean createWaypointReflect(
            ResourceLocation dimension,
            int x,
            int y,
            int z,
            String label
    ) throws ReflectiveOperationException {
        Class<?> builtInHudModules = Class.forName("xaero.hud.minimap.BuiltInHudModules");
        Object minimapModule = builtInHudModules.getField("MINIMAP").get(null);
        Object session = minimapModule.getClass().getMethod("getCurrentSession").invoke(minimapModule);
        if (session == null) {
            return false;
        }

        Object worldManager = session.getClass().getMethod("getWorldManager").invoke(session);
        Object world = resolveMinimapWorld(session, worldManager, dimension);
        if (world == null) {
            return false;
        }

        Object waypointSet = world.getClass().getMethod("getCurrentWaypointSet").invoke(world);
        if (waypointSet == null) {
            return false;
        }

        removeExistingMigrationWaypoints(waypointSet);

        Class<?> waypointColorClass = Class.forName("xaero.hud.minimap.waypoint.WaypointColor");
        Object aquaColor = Enum.valueOf(waypointColorClass.asSubclass(Enum.class), "AQUA");

        Class<?> waypointPurposeClass = Class.forName("xaero.hud.minimap.waypoint.WaypointPurpose");
        Object normalPurpose = Enum.valueOf(waypointPurposeClass.asSubclass(Enum.class), "NORMAL");

        Class<?> waypointClass = Class.forName("xaero.common.minimap.waypoints.Waypoint");
        Object waypoint = waypointClass.getConstructor(
                int.class,
                int.class,
                int.class,
                String.class,
                String.class,
                waypointColorClass,
                waypointPurposeClass,
                boolean.class
        ).newInstance(x, y, z, label, initials(label), aquaColor, normalPurpose, true);

        waypointSet.getClass().getMethod("add", waypointClass).invoke(waypointSet, waypoint);

        Object worldManagerIo = session.getClass().getMethod("getWorldManagerIO").invoke(session);
        worldManagerIo.getClass().getMethod("saveWorld", world.getClass()).invoke(worldManagerIo, world);
        return true;
    }

    private static Object resolveMinimapWorld(
            Object session,
            Object worldManager,
            ResourceLocation dimension
    ) throws ReflectiveOperationException {
        Object currentWorld = worldManager.getClass().getMethod("getCurrentWorld").invoke(worldManager);
        if (currentWorld != null && dimensionMatches(currentWorld, dimension)) {
            return currentWorld;
        }

        ResourceKey<Level> dimensionKey = ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION,
                dimension
        );
        Object resolved = resolveWorldForDimension(session, worldManager, dimensionKey);
        if (resolved != null) {
            return resolved;
        }
        return currentWorld;
    }

    private static Object resolveWorldForDimension(
            Object session,
            Object worldManager,
            ResourceKey<Level> dimension
    ) throws ReflectiveOperationException {
        Object rootContainer = worldManager.getClass().getMethod("getCurrentRootContainer").invoke(worldManager);
        for (Object world : (Iterable<?>) rootContainer.getClass().getMethod("getWorlds").invoke(rootContainer)) {
            if (dimensionMatches(world, dimension.location())) {
                return world;
            }
        }
        for (Object subContainer : (Iterable<?>) rootContainer.getClass().getMethod("getSubContainers").invoke(rootContainer)) {
            for (Object world : (Iterable<?>) subContainer.getClass().getMethod("getWorlds").invoke(subContainer)) {
                if (dimensionMatches(world, dimension.location())) {
                    return world;
                }
            }
        }

        Object dimensionHelper = session.getClass().getMethod("getDimensionHelper").invoke(session);
        String dimensionDirectoryName = (String) dimensionHelper.getClass()
                .getMethod("getDimensionDirectoryName", ResourceKey.class)
                .invoke(dimensionHelper, dimension);
        Object worldStateUpdater = session.getClass().getMethod("getWorldStateUpdater").invoke(session);
        String worldNode = (String) worldStateUpdater.getClass()
                .getMethod("getPotentialWorldNode", ResourceKey.class, boolean.class)
                .invoke(worldStateUpdater, dimension, true);
        Object worldState = session.getClass().getMethod("getWorldState").invoke(session);
        Object autoRootPath = worldState.getClass().getMethod("getAutoRootContainerPath").invoke(worldState);
        Object containerPath = autoRootPath.getClass()
                .getMethod("resolve", String.class)
                .invoke(autoRootPath, dimensionDirectoryName);
        containerPath = containerPath.getClass().getMethod("resolve", String.class).invoke(containerPath, worldNode);
        return worldManager.getClass().getMethod("getWorld", containerPath.getClass()).invoke(worldManager, containerPath);
    }

    private static boolean dimensionMatches(Object minimapWorld, ResourceLocation dimension) throws ReflectiveOperationException {
        Object dimId = minimapWorld.getClass().getMethod("getDimId").invoke(minimapWorld);
        if (!(dimId instanceof ResourceKey<?> key)) {
            return false;
        }
        return key.location().equals(dimension);
    }

    private static void removeExistingMigrationWaypoints(Object waypointSet) throws ReflectiveOperationException {
        Iterable<?> waypoints = (Iterable<?>) waypointSet.getClass().getMethod("getWaypoints").invoke(waypointSet);
        Class<?> waypointClass = Class.forName("xaero.common.minimap.waypoints.Waypoint");
        for (Object waypoint : waypoints) {
            String name = (String) waypointClass.getMethod("getName").invoke(waypoint);
            if (name != null && name.startsWith(MIGRATION_PREFIX)) {
                waypointSet.getClass().getMethod("remove", waypointClass).invoke(waypointSet, waypoint);
            }
        }
    }

    private static String sanitize(String label) {
        String trimmed = label.replace('[', ' ').replace(']', ' ').trim();
        if (trimmed.isEmpty()) {
            return MIGRATION_PREFIX;
        }
        if (!trimmed.startsWith(MIGRATION_PREFIX)) {
            return MIGRATION_PREFIX + " · " + trimmed;
        }
        return trimmed;
    }

    private static String initials(String label) {
        for (int index = 0; index < label.length(); index++) {
            char character = label.charAt(index);
            if (Character.isLetterOrDigit(character)) {
                return String.valueOf(Character.toUpperCase(character));
            }
        }
        return "M";
    }
}
