package nl.streats1.ancientextensions.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Optional Lootr integration — converts the filled camp chest into a per-player Lootr inventory
 * (same behaviour as {@code /lootr custom}) when enabled in config and Lootr is installed.
 */
public final class LootrCampChestCompat {

    private static final Logger LOGGER = LoggerFactory.getLogger("ancient_extensions");
    private static final Method CONVERT_TO_CUSTOM;
    private static final Method LOOTR_IS_READY;
    private static final boolean LOOTR_CLASSES_PRESENT;

    static {
        Method convert = null;
        Method isReady = null;
        boolean present = false;
        try {
            Class.forName("noobanidus.mods.lootr.common.api.LootrAPI");
            Class<?> commandLootr = Class.forName("noobanidus.mods.lootr.common.command.CommandLootr");
            Class<?> lootrApi = Class.forName("noobanidus.mods.lootr.common.api.LootrAPI");
            convert = commandLootr.getMethod(
                    "convertToCustom",
                    BlockPos.class,
                    ServerLevel.class,
                    Consumer.class,
                    HolderLookup.Provider.class
            );
            isReady = lootrApi.getMethod("isReady");
            present = true;
        } catch (ReflectiveOperationException ignored) {
            // Lootr not installed — compat stays inactive.
        }
        CONVERT_TO_CUSTOM = convert;
        LOOTR_IS_READY = isReady;
        LOOTR_CLASSES_PRESENT = present;
    }

    private LootrCampChestCompat() {
    }

    public static boolean isLootrInstalled() {
        return LOOTR_CLASSES_PRESENT;
    }

    public static boolean isLootrReady() {
        if (!LOOTR_CLASSES_PRESENT || LOOTR_IS_READY == null) {
            return false;
        }
        try {
            Object ready = LOOTR_IS_READY.invoke(null);
            return ready instanceof Boolean bool && bool;
        } catch (ReflectiveOperationException exception) {
            return false;
        }
    }

    /**
     * Converts a filled vanilla camp chest into a Lootr custom inventory chest.
     *
     * @return true when conversion succeeded
     */
    public static boolean convertFilledChest(ServerLevel level, BlockPos chestPos) {
        if (!LOOTR_CLASSES_PRESENT || CONVERT_TO_CUSTOM == null || !isLootrReady()) {
            return false;
        }
        try {
            Object result = CONVERT_TO_CUSTOM.invoke(
                    null,
                    chestPos,
                    level,
                    (Consumer<String>) message -> LOGGER.debug("Lootr camp chest: {}", message),
                    level.registryAccess()
            );
            return result instanceof Boolean converted && converted;
        } catch (ReflectiveOperationException exception) {
            LOGGER.warn("Failed to convert camp chest to Lootr at {}", chestPos, exception);
            return false;
        }
    }
}
