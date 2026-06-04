package nl.streats1.ancientextensions.util;

/**
 * Loader-agnostic mod presence checks without a hard dependency on Architectury API.
 */
public final class ModPresence {

    private ModPresence() {
    }

    public static boolean isLoaded(String modId) {
        if (tryFabric(modId)) {
            return true;
        }
        return tryNeoForge(modId);
    }

    private static boolean tryFabric(String modId) {
        try {
            Class<?> loader = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object instance = loader.getMethod("getInstance").invoke(null);
            return Boolean.TRUE.equals(loader.getMethod("isModLoaded", String.class).invoke(instance, modId));
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static boolean tryNeoForge(String modId) {
        try {
            Class<?> modList = Class.forName("net.neoforged.fml.ModList");
            Object instance = modList.getMethod("get").invoke(null);
            return Boolean.TRUE.equals(modList.getMethod("isLoaded", String.class).invoke(instance, modId));
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }
}
