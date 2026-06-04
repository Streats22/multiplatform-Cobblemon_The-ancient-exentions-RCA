package nl.streats1.ancientextensions.config;

/**
 * Runtime Shiny Charm settings (loaded from platform config on startup).
 */
public final class ShinyCharmConfig {

    private static boolean enabled = true;
    /**
     * Divides Cobblemon's shiny rate denominator while active (3 = roughly triple odds, like Gen V–VII).
     */
    private static float rateMultiplier = 3.0F;
    /**
     * When true, the claimed charm item must remain in the player's inventory for the bonus.
     */
    private static boolean requireCharmInInventory = true;

    private ShinyCharmConfig() {
    }

    public static boolean enabled() {
        return enabled;
    }

    public static float rateMultiplier() {
        return rateMultiplier;
    }

    public static boolean requireCharmInInventory() {
        return requireCharmInInventory;
    }

    public static void apply(boolean enabledValue, float multiplier, boolean requireInInventory) {
        enabled = enabledValue;
        rateMultiplier = Math.max(1.0F, multiplier);
        requireCharmInInventory = requireInInventory;
    }
}
