package nl.streats1.ancientextensions.config;

/**
 * Runtime passport / origin settings (loaded from platform config on startup).
 */
public final class PassportConfig {

    private static boolean openOriginPickerOnJoin = true;
    private static boolean deferOriginPickerForMca = true;

    private PassportConfig() {
    }

    /**
     * When true, players without a stamped origin are prompted with the passport picker on join.
     */
    public static boolean openOriginPickerOnJoin() {
        return openOriginPickerOnJoin;
    }

    /**
     * When MCA Reborn ({@code mca}) is installed, wait for the destiny intro to finish before opening
     * the passport picker (avoids two fullscreen flows at once).
     */
    public static boolean deferOriginPickerForMca() {
        return deferOriginPickerForMca;
    }

    public static void apply(boolean openPickerOnJoin) {
        openOriginPickerOnJoin = openPickerOnJoin;
    }

    public static void apply(boolean openPickerOnJoin, boolean deferForMca) {
        openOriginPickerOnJoin = openPickerOnJoin;
        deferOriginPickerForMca = deferForMca;
    }
}
