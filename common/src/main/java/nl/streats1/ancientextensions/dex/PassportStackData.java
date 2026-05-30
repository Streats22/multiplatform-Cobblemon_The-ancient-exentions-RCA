package nl.streats1.ancientextensions.dex;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Optional;

/**
 * Mirrors survey origin on passport item stacks for client tooltips.
 */
public final class PassportStackData {

    private static final String ORIGIN_KEY = "surveyOrigin";
    private static final String TOWN_KEY = "surveyOriginTown";

    private PassportStackData() {
    }

    public static void writeOrigin(ItemStack stack, SurveyRegion region, SurveyOriginTown town) {
        CompoundTag tag = new CompoundTag();
        tag.putString(ORIGIN_KEY, region.getId());
        if (town != null) {
            tag.putString(TOWN_KEY, town.getId());
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static Optional<SurveyRegion> readOrigin(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) {
            return Optional.empty();
        }
        CompoundTag tag = custom.copyTag();
        if (!tag.contains(ORIGIN_KEY)) {
            return Optional.empty();
        }
        return SurveyRegion.fromId(tag.getString(ORIGIN_KEY));
    }

    public static Optional<SurveyOriginTown> readOriginTown(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) {
            return Optional.empty();
        }
        CompoundTag tag = custom.copyTag();
        if (!tag.contains(TOWN_KEY)) {
            return Optional.empty();
        }
        return SurveyOriginTown.fromId(tag.getString(TOWN_KEY));
    }
}
