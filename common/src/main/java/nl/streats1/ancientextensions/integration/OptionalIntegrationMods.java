package nl.streats1.ancientextensions.integration;

import nl.streats1.ancientextensions.util.ModLoaderRuntime;
import nl.streats1.ancientextensions.util.ModPresence;

/**
 * Optional pack mods — feature hooks run only when the mod is loaded <em>and</em> supported on the
 * current loader. See {@code docs/DEPENDENCIES.md} for the full NeoForge / Fabric matrix.
 */
public final class OptionalIntegrationMods {

    public static final String CREATE = "create";
    public static final String SOPHISTICATED_BACKPACKS = "sophisticatedbackpacks";
    public static final String REGIONS_UNEXPLORED = "regions_unexplored";
    public static final String BIOMES_O_PLENTY = "biomesoplenty";

    private OptionalIntegrationMods() {
    }

    /**
     * Official Create for 1.21.1 is NeoForge-only. Display Link integration and sensor recipes require it.
     */
    public static boolean hasCreate() {
        return ModLoaderRuntime.isNeoForge() && ModPresence.isLoaded(CREATE);
    }

    /**
     * Field Survey Telemetry upgrade — official Sophisticated Backpacks on NeoForge only.
     * <p>
     * An unofficial Fabric port also registers {@code sophisticatedbackpacks} / {@code sophisticatedcore}
     * (see Modrinth), but {@link ModPresence#isLoaded} alone is not enough: our upgrade uses NeoForge-only
     * integration code and official SB API jars. Fabric packs with the community port get normal backpacks,
     * not the Ancient Extensions telemetry upgrade, until a separate Fabric integration is added.
     */
    public static boolean hasSophisticatedBackpacks() {
        return ModLoaderRuntime.isNeoForge() && ModPresence.isLoaded(SOPHISTICATED_BACKPACKS);
    }

    /**
     * Biome routes and spawn filtering — both loaders when the mod is in the pack.
     */
    public static boolean hasRegionsUnexplored() {
        return ModPresence.isLoaded(REGIONS_UNEXPLORED);
    }

    public static boolean hasBiomesOPlenty() {
        return ModPresence.isLoaded(BIOMES_O_PLENTY);
    }
}
