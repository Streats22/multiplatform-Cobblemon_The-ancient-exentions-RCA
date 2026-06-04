package nl.streats1.ancientextensions.neoforge.integration.sophisticated;

import nl.streats1.ancientextensions.integration.OptionalIntegrationMods;

/**
 * Optional Sophisticated Backpacks integration — upgrade item registers only when SB is loaded.
 */
public final class SophisticatedBackpacksCompat {

    private SophisticatedBackpacksCompat() {
    }

    public static void init() {
        if (!OptionalIntegrationMods.hasSophisticatedBackpacks()) {
            return;
        }
        // Upgrade type is bound via FieldSurveyTelemetryUpgradeItem.getType(); no extra registry hooks required.
    }

    public static boolean isActive() {
        return OptionalIntegrationMods.hasSophisticatedBackpacks();
    }
}
