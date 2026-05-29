package com.streats.ancientextensions.dex;

import com.streats.ancientextensions.migration.MigrationSeason;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Per-player Regional Survey progress. Species are tracked on {@linkplain com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent capture} only.
 */
public class RegionalSurveyData {

    private static final String NBT_ROOT = "AncientExtensionsRegionalSurvey";

    private final Set<ResourceLocation> caughtSpecies = new HashSet<>();
    private int researchPoints;
    /** True after the player used the kit item to pitch camp (one-time). */
    private boolean professorsKitDeployed;
    /** True after the starter kit item was granted on first join to this world. */
    private boolean starterKitGranted;

    private String trackedMigrationSeason = MigrationSeason.SPRING.name();
    private int migrationLegIndex;
    private int currentLegCatches;
    private final Map<MigrationSeason, Integer> migrationCompletions = new EnumMap<>(MigrationSeason.class);

    public static RegionalSurveyData load(CompoundTag tag) {
        RegionalSurveyData data = new RegionalSurveyData();
        if (tag == null || tag.isEmpty()) {
            return data;
        }
        data.researchPoints = tag.getInt("researchPoints");
        data.professorsKitDeployed = tag.getBoolean("professorsKitDeployed");
        if (!tag.contains("professorsKitDeployed") && tag.getBoolean("receivedProfessorsKit")) {
            data.professorsKitDeployed = true;
        }
        data.starterKitGranted = tag.getBoolean("starterKitGranted");
        data.trackedMigrationSeason = tag.getString("trackedMigrationSeason");
        data.migrationLegIndex = tag.getInt("migrationLegIndex");
        data.currentLegCatches = tag.getInt("currentLegCatches");

        ListTag species = tag.getList("caughtSpecies", Tag.TAG_STRING);
        for (Tag entry : species) {
            data.caughtSpecies.add(ResourceLocation.parse(entry.getAsString()));
        }

        CompoundTag completions = tag.getCompound("migrationCompletions");
        for (MigrationSeason season : MigrationSeason.values()) {
            if (completions.contains(season.name())) {
                data.migrationCompletions.put(season, completions.getInt(season.name()));
            }
        }
        return data;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("researchPoints", researchPoints);
        tag.putBoolean("professorsKitDeployed", professorsKitDeployed);
        tag.putBoolean("starterKitGranted", starterKitGranted);
        tag.putString("trackedMigrationSeason", trackedMigrationSeason);
        tag.putInt("migrationLegIndex", migrationLegIndex);
        tag.putInt("currentLegCatches", currentLegCatches);

        ListTag species = new ListTag();
        for (ResourceLocation id : caughtSpecies) {
            species.add(StringTag.valueOf(id.toString()));
        }
        tag.put("caughtSpecies", species);

        CompoundTag completions = new CompoundTag();
        for (Map.Entry<MigrationSeason, Integer> entry : migrationCompletions.entrySet()) {
            completions.putInt(entry.getKey().name(), entry.getValue());
        }
        tag.put("migrationCompletions", completions);
        return tag;
    }

    /**
     * Catch-only dex: registers a species the first time it is captured in a battle/capture event.
     */
    public boolean registerCaughtSpecies(ResourceLocation speciesId, int pointsForNew) {
        if (!caughtSpecies.add(speciesId)) {
            return false;
        }
        researchPoints += pointsForNew;
        return true;
    }

    public void addResearchPoints(int amount) {
        researchPoints += amount;
    }

    public int getResearchPoints() {
        return researchPoints;
    }

    public int getCaughtSpeciesCount() {
        return caughtSpecies.size();
    }

    public ResearchTier getTier() {
        return ResearchTier.fromPoints(researchPoints);
    }

    public boolean hasDeployedProfessorsKit() {
        return professorsKitDeployed;
    }

    public void markProfessorsKitDeployed() {
        professorsKitDeployed = true;
    }

    public boolean hasStarterKitGranted() {
        return starterKitGranted;
    }

    public void markStarterKitGranted() {
        starterKitGranted = true;
    }

    public MigrationSeason getTrackedMigrationSeason() {
        try {
            return MigrationSeason.valueOf(trackedMigrationSeason);
        } catch (IllegalArgumentException ex) {
            return MigrationSeason.SPRING;
        }
    }

    public void syncMigrationSeason(MigrationSeason season) {
        if (season.name().equals(trackedMigrationSeason)) {
            return;
        }
        trackedMigrationSeason = season.name();
        migrationLegIndex = 0;
        currentLegCatches = 0;
    }

    public int getMigrationLegIndex() {
        return migrationLegIndex;
    }

    public int getCurrentLegCatches() {
        return currentLegCatches;
    }

    public void recordLegCatch() {
        currentLegCatches++;
    }

    public void advanceMigrationLeg() {
        migrationLegIndex++;
        currentLegCatches = 0;
    }

    public int getMigrationCompletions(MigrationSeason season) {
        return migrationCompletions.getOrDefault(season, 0);
    }

    public void recordMigrationCompletion(MigrationSeason season, int routeRewardRp) {
        migrationCompletions.merge(season, 1, Integer::sum);
        migrationLegIndex = 0;
        currentLegCatches = 0;
        researchPoints += routeRewardRp;
    }
}
