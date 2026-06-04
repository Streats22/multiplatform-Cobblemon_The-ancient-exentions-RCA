package nl.streats1.ancientextensions.neoforge.integration.sophisticated;

import nl.streats1.ancientextensions.integration.OptionalIntegrationMods;

/**
 * Optional Sophisticated Backpacks integration.
 * <p>
 * The telemetry upgrade item extends {@code UpgradeItemBase}, but SB's {@code BackpackWrapper}
 * also requires items to be tagged {@code sophisticatedbackpacks:upgrade} before they can be
 * inserted into backpack upgrade slots — see {@code data/sophisticatedbackpacks/tags/item/upgrade.json}.
 */
public final class SophisticatedBackpacksCompat {

    private SophisticatedBackpacksCompat() {
    }

    public static void init() {
        if (!OptionalIntegrationMods.hasSophisticatedBackpacks()) {
            return;
        }
        // Item + tag registration is sufficient; wrapper logic binds via IUpgradeItem.getType().
    }

    public static boolean isActive() {
        return OptionalIntegrationMods.hasSophisticatedBackpacks();
    }
}
