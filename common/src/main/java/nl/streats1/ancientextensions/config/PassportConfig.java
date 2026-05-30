package nl.streats1.ancientextensions.config;

/**
 * Runtime passport / origin settings (loaded from platform config on startup).
 */
public final class PassportConfig {

    private static boolean openOriginPickerOnJoin = true;

    private PassportConfig() {
    }

    /** When true, players without a stamped origin are prompted with the passport picker on join. */
    public static boolean openOriginPickerOnJoin() {
        return openOriginPickerOnJoin;
    }

    public static void apply(boolean openPickerOnJoin) {
        openOriginPickerOnJoin = openPickerOnJoin;
    }
}
