package nl.streats1.ancientextensions.migration;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * One stop on a seasonal migration route. Any listed biome counts (Terralith, Regions Unexplored, or vanilla).
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
        return biomeIds.contains(biomeId);
    }

    public String biomeLabel() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < biomeIds.size(); i++) {
            if (i > 0) {
                builder.append(" / ");
            }
            builder.append(biomeIds.get(i));
        }
        return builder.toString();
    }
}
