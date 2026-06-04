package nl.streats1.ancientextensions.migration;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nl.streats1.ancientextensions.integration.OptionalIntegrationMods;

/**
 * Builds seasonal migration biome lists from optional world-gen mods.
 * When neither Regions Unexplored nor Biomes O' Plenty is loaded, {@link VanillaSeasonBiomes} is used.
 */
public final class MigrationBiomeCatalog {

    private MigrationBiomeCatalog() {
    }

    public static List<ResourceLocation> biomesForSeason(MigrationSeason season) {
        List<ResourceLocation> biomes = new ArrayList<>();
        if (OptionalIntegrationMods.hasRegionsUnexplored()) {
            biomes.addAll(RegionsUnexploredBiomes.forSeason(season));
        }
        if (OptionalIntegrationMods.hasBiomesOPlenty()) {
            biomes.addAll(BiomesOPlentyBiomes.forSeason(season));
        }
        if (biomes.isEmpty()) {
            return VanillaSeasonBiomes.forSeason(season);
        }
        return List.copyOf(biomes);
    }

    public static boolean isActiveBiomeNamespace(String namespace) {
        if (namespace == null) {
            return false;
        }
        return switch (namespace) {
            case "minecraft" -> true;
            case RegionsUnexploredBiomes.NAMESPACE -> OptionalIntegrationMods.hasRegionsUnexplored();
            case BiomesOPlentyBiomes.NAMESPACE -> OptionalIntegrationMods.hasBiomesOPlenty();
            default -> false;
        };
    }

    public static boolean isRoutableModBiome(ResourceLocation biomeId) {
        return biomeId != null && isActiveBiomeNamespace(biomeId.getNamespace());
    }

    /**
     * Namespaces used when filtering spawn-pool biome lists at runtime.
     */
    public static Set<String> activeSpawnNamespaces() {
        Set<String> namespaces = new LinkedHashSet<>();
        namespaces.add("minecraft");
        if (OptionalIntegrationMods.hasRegionsUnexplored()) {
            namespaces.add(RegionsUnexploredBiomes.NAMESPACE);
        }
        if (OptionalIntegrationMods.hasBiomesOPlenty()) {
            namespaces.add(BiomesOPlentyBiomes.NAMESPACE);
        }
        return Set.copyOf(namespaces);
    }

    public static Component activeWorldGenLabel() {
        boolean ru = OptionalIntegrationMods.hasRegionsUnexplored();
        boolean bop = OptionalIntegrationMods.hasBiomesOPlenty();
        if (ru && bop) {
            return Component.translatable("ancient_extensions.migration.worldgen.ru_and_bop");
        }
        if (ru) {
            return Component.translatable("ancient_extensions.migration.worldgen.regions_unexplored");
        }
        if (bop) {
            return Component.translatable("ancient_extensions.migration.worldgen.biomes_o_plenty");
        }
        return Component.translatable("ancient_extensions.migration.worldgen.vanilla");
    }
}
