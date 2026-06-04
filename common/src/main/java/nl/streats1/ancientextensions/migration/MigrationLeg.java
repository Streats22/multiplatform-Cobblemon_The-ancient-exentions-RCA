package nl.streats1.ancientextensions.migration;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * One stop on a seasonal migration route.
 * Listed mod biomes count when their mod is loaded; any vanilla ({@code minecraft:}) biome counts too
 * (including when using {@link RegionsUnexploredExpansion}).
 */
public record MigrationLeg(
        List<ResourceLocation> biomeIds,
        int requiredCatches,
        int bonusResearchPoints
) {
    public boolean matchesBiome(ResourceLocation biomeId) {
        if (biomeId == null) {
            return false;
        }
        if (MigrationBiomeContext.isVanillaBiome(biomeId)) {
            return true;
        }
        if (!MigrationBiomeCatalog.isRoutableModBiome(biomeId)) {
            return false;
        }
        return biomeIds.contains(biomeId);
    }

    /** Human-readable biome list for chat and commands. */
    public String biomeLabel() {
        return regionsUnexploredLabel();
    }

    /** Short journal line — catch target and biome count without listing every biome. */
    public String journalLegSummary() {
        return requiredCatches() + " migratory catches · " + biomeIds().size() + " route biomes";
    }

    public Component biomeLabelComponent() {
        return Component.translatable(
                "ancient_extensions.migration.leg_biomes",
                regionsUnexploredLabel()
        );
    }

    private String regionsUnexploredLabel() {
        return biomeIds.stream()
                .map(MigrationLeg::prettyBiomeName)
                .collect(Collectors.joining(" · "));
    }

    private static String prettyBiomeName(ResourceLocation id) {
        String path = id.getPath().replace('_', ' ');
        String[] words = path.split(" ");
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                name.append(' ');
            }
            String word = words[i];
            if (!word.isEmpty()) {
                name.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    name.append(word.substring(1).toLowerCase(Locale.ROOT));
                }
            }
        }
        return name.toString();
    }
}
