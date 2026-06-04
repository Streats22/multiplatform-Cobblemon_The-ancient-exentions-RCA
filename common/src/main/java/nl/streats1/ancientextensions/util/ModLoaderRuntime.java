package nl.streats1.ancientextensions.util;

/**
 * Which mod loader is running this jar (NeoForge vs Fabric), without a compile dependency on either API.
 */
public enum ModLoaderRuntime {
    NEOFORGE,
    FABRIC,
    UNKNOWN;

    private static final ModLoaderRuntime CURRENT = detect();

    public static ModLoaderRuntime get() {
        return CURRENT;
    }

    public static boolean isNeoForge() {
        return CURRENT == NEOFORGE;
    }

    public static boolean isFabric() {
        return CURRENT == FABRIC;
    }

    private static ModLoaderRuntime detect() {
        if (classPresent("net.neoforged.fml.ModList")) {
            return NEOFORGE;
        }
        if (classPresent("net.fabricmc.loader.api.FabricLoader")) {
            return FABRIC;
        }
        return UNKNOWN;
    }

    private static boolean classPresent(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
