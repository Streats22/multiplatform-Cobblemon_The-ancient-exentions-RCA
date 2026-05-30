package nl.streats1.ancientextensions.dex;

import nl.streats1.ancientextensions.migration.MigrationSeason;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

/**
 * Seasonal migration route progress.
 */
public final class MigrationProgress {

    private String trackedMigrationSeason = MigrationSeason.SPRING.name();
    private int migrationLegIndex;
    private int currentLegCatches;
    private final Map<MigrationSeason, Integer> migrationCompletions = new EnumMap<>(MigrationSeason.class);

    void load(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return;
        }
        trackedMigrationSeason = tag.getString("trackedMigrationSeason");
        migrationLegIndex = tag.getInt("migrationLegIndex");
        currentLegCatches = tag.getInt("currentLegCatches");
        migrationCompletions.clear();
        CompoundTag completions = tag.getCompound("migrationCompletions");
        for (MigrationSeason season : MigrationSeason.values()) {
            if (completions.contains(season.name())) {
                migrationCompletions.put(season, completions.getInt(season.name()));
            }
        }
    }

    void loadLegacy(CompoundTag tag) {
        trackedMigrationSeason = tag.getString("trackedMigrationSeason");
        migrationLegIndex = tag.getInt("migrationLegIndex");
        currentLegCatches = tag.getInt("currentLegCatches");
        migrationCompletions.clear();
        CompoundTag completions = tag.getCompound("migrationCompletions");
        for (MigrationSeason season : MigrationSeason.values()) {
            if (completions.contains(season.name())) {
                migrationCompletions.put(season, completions.getInt(season.name()));
            }
        }
    }

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("trackedMigrationSeason", trackedMigrationSeason);
        tag.putInt("migrationLegIndex", migrationLegIndex);
        tag.putInt("currentLegCatches", currentLegCatches);
        CompoundTag completions = new CompoundTag();
        for (Map.Entry<MigrationSeason, Integer> entry : migrationCompletions.entrySet()) {
            completions.putInt(entry.getKey().name(), entry.getValue());
        }
        tag.put("migrationCompletions", completions);
        return tag;
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

    public void recordMigrationCompletion(MigrationSeason season, int routeRewardRp, SurveyProgress survey) {
        migrationCompletions.merge(season, 1, Integer::sum);
        migrationLegIndex = 0;
        currentLegCatches = 0;
        survey.addResearchPoints(routeRewardRp);
    }
}
