package nl.streats1.ancientextensions.dex;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Catch-only dex progress and survey origin.
 */
public final class SurveyProgress {

    private final Set<ResourceLocation> caughtSpecies = new HashSet<>();
    private final Set<String> claimedTierRewards = new HashSet<>();
    private int researchPoints;
    private String surveyOrigin = "";
    private String surveyOriginTown = "";
    private boolean originSetupMode = false;

    void load(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return;
        }
        researchPoints = tag.getInt("researchPoints");
        if (tag.contains("surveyOrigin")) {
            surveyOrigin = tag.getString("surveyOrigin");
        }
        if (tag.contains("surveyOriginTown")) {
            surveyOriginTown = tag.getString("surveyOriginTown");
        }
        if (tag.contains("originSetupMode")) {
            originSetupMode = tag.getBoolean("originSetupMode");
        }
        if (tag.contains("claimedTierRewards")) {
            claimedTierRewards.clear();
            ListTag claimed = tag.getList("claimedTierRewards", Tag.TAG_STRING);
            for (Tag entry : claimed) {
                claimedTierRewards.add(entry.getAsString());
            }
        }
        ListTag species = tag.getList("caughtSpecies", Tag.TAG_STRING);
        caughtSpecies.clear();
        for (Tag entry : species) {
            caughtSpecies.add(ResourceLocation.parse(entry.getAsString()));
        }
    }

    void loadLegacy(CompoundTag tag) {
        researchPoints = tag.getInt("researchPoints");
        if (tag.contains("surveyOrigin")) {
            surveyOrigin = tag.getString("surveyOrigin");
        }
        if (tag.contains("surveyOriginTown")) {
            surveyOriginTown = tag.getString("surveyOriginTown");
        }
        if (tag.contains("originSetupMode")) {
            originSetupMode = tag.getBoolean("originSetupMode");
        }
        if (tag.contains("claimedTierRewards")) {
            claimedTierRewards.clear();
            ListTag claimed = tag.getList("claimedTierRewards", Tag.TAG_STRING);
            for (Tag entry : claimed) {
                claimedTierRewards.add(entry.getAsString());
            }
        }
        ListTag species = tag.getList("caughtSpecies", Tag.TAG_STRING);
        caughtSpecies.clear();
        for (Tag entry : species) {
            caughtSpecies.add(ResourceLocation.parse(entry.getAsString()));
        }
    }

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("researchPoints", researchPoints);
        if (!surveyOrigin.isEmpty()) {
            tag.putString("surveyOrigin", surveyOrigin);
        }
        if (!surveyOriginTown.isEmpty()) {
            tag.putString("surveyOriginTown", surveyOriginTown);
        }
        if (originSetupMode) {
            tag.putBoolean("originSetupMode", true);
        }
        if (!claimedTierRewards.isEmpty()) {
            ListTag claimed = new ListTag();
            for (String tierId : claimedTierRewards) {
                claimed.add(StringTag.valueOf(tierId));
            }
            tag.put("claimedTierRewards", claimed);
        }
        ListTag species = new ListTag();
        for (ResourceLocation id : caughtSpecies) {
            species.add(StringTag.valueOf(id.toString()));
        }
        tag.put("caughtSpecies", species);
        return tag;
    }

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

    public Optional<SurveyRegion> getSurveyOrigin() {
        return SurveyRegion.fromId(surveyOrigin);
    }

    public void setSurveyOrigin(SurveyRegion region) {
        surveyOrigin = region.getId();
    }

    public Optional<SurveyOriginTown> getSurveyOriginTown() {
        return SurveyOriginTown.fromId(surveyOriginTown);
    }

    public void setSurveyOriginTown(SurveyOriginTown town) {
        surveyOriginTown = town.getId();
    }

    public void clearSurveyOriginTown() {
        surveyOriginTown = "";
    }

    public boolean isOriginSetupMode() {
        return originSetupMode;
    }

    public void setOriginSetupMode(boolean originSetupMode) {
        this.originSetupMode = originSetupMode;
    }

    public boolean hasClaimedTierReward(ResearchTier tier) {
        return claimedTierRewards.contains(tier.name());
    }

    public void markTierRewardClaimed(ResearchTier tier) {
        claimedTierRewards.add(tier.name());
    }
}
