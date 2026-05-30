package nl.streats1.ancientextensions.dex;

import nl.streats1.ancientextensions.migration.MigrationSeason;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Per-player Regional Survey aggregate. Composes {@link SurveyProgress}, {@link KitProgress},
 * and {@link MigrationProgress} for a single platform attachment / save root.
 */
public class RegionalSurveyData {

    private final SurveyProgress survey = new SurveyProgress();
    private final KitProgress kit = new KitProgress();
    private final MigrationProgress migration = new MigrationProgress();

    public static RegionalSurveyData load(CompoundTag tag) {
        RegionalSurveyData data = new RegionalSurveyData();
        if (tag == null || tag.isEmpty()) {
            return data;
        }
        if (tag.contains("survey")) {
            data.survey.load(tag.getCompound("survey"));
            data.kit.load(tag.getCompound("kit"));
            data.migration.load(tag.getCompound("migration"));
        } else {
            data.survey.loadLegacy(tag);
            data.kit.loadLegacy(tag);
            data.migration.loadLegacy(tag);
        }
        return data;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.put("survey", survey.save());
        tag.put("kit", kit.save());
        tag.put("migration", migration.save());
        return tag;
    }

    public boolean registerCaughtSpecies(ResourceLocation speciesId, int pointsForNew) {
        return survey.registerCaughtSpecies(speciesId, pointsForNew);
    }

    public void addResearchPoints(int amount) {
        survey.addResearchPoints(amount);
    }

    public int getResearchPoints() {
        return survey.getResearchPoints();
    }

    public int getCaughtSpeciesCount() {
        return survey.getCaughtSpeciesCount();
    }

    public ResearchTier getTier() {
        return survey.getTier();
    }

    public boolean hasDeployedProfessorsKit() {
        return kit.hasDeployedProfessorsKit();
    }

    public void markProfessorsKitDeployed() {
        kit.markProfessorsKitDeployed();
    }

    public boolean hasStarterKitGranted() {
        return kit.hasStarterKitGranted();
    }

    public void markStarterKitGranted() {
        kit.markStarterKitGranted();
    }

    public Optional<SurveyRegion> getSurveyOrigin() {
        return survey.getSurveyOrigin();
    }

    public void setSurveyOrigin(SurveyRegion region) {
        survey.setSurveyOrigin(region);
    }

    public Optional<SurveyOriginTown> getSurveyOriginTown() {
        return survey.getSurveyOriginTown();
    }

    public void setSurveyOriginTown(SurveyOriginTown town) {
        survey.setSurveyOriginTown(town);
    }

    public void clearSurveyOriginTown() {
        survey.clearSurveyOriginTown();
    }

    public boolean isOriginSetupMode() {
        return survey.isOriginSetupMode();
    }

    public void setOriginSetupMode(boolean originSetupMode) {
        survey.setOriginSetupMode(originSetupMode);
    }

    /** Show the region/town picker on the passport even when origin is already stamped. */
    public boolean showsPassportSetupScreen() {
        return isOriginSetupMode() || getSurveyOrigin().isEmpty();
    }

    public MigrationSeason getTrackedMigrationSeason() {
        return migration.getTrackedMigrationSeason();
    }

    public void syncMigrationSeason(MigrationSeason season) {
        migration.syncMigrationSeason(season);
    }

    public int getMigrationLegIndex() {
        return migration.getMigrationLegIndex();
    }

    public int getCurrentLegCatches() {
        return migration.getCurrentLegCatches();
    }

    public void recordLegCatch() {
        migration.recordLegCatch();
    }

    public void advanceMigrationLeg() {
        migration.advanceMigrationLeg();
    }

    public int getMigrationCompletions(MigrationSeason season) {
        return migration.getMigrationCompletions(season);
    }

    public void recordMigrationCompletion(MigrationSeason season, int routeRewardRp) {
        migration.recordMigrationCompletion(season, routeRewardRp, survey);
    }
}
