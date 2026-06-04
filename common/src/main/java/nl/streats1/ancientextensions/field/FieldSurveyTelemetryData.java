package nl.streats1.ancientextensions.field;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

/** Cached migration readout stored on a backpack upgrade stack (optional SB integration). */
public final class FieldSurveyTelemetryData {

    private static final String ROOT = "ancient_extensions_field_telemetry";
    private static final String SEASON = "season";
    private static final String BIOME_ROUTE = "biome_route";
    private static final String SPECIES = "species";
    private static final String UPDATED_AT = "updated_at";

    private FieldSurveyTelemetryData() {
    }

    public static void write(ItemStack stack, FieldSurveyWorldSnapshot snapshot, long gameTime) {
        CompoundTag tag = root(stack);
        tag.putString(SEASON, snapshot.seasonLine());
        tag.putString(BIOME_ROUTE, snapshot.biomeRouteLine());
        tag.putString(SPECIES, snapshot.speciesLine());
        tag.putLong(UPDATED_AT, gameTime);
        apply(stack, tag);
    }

    @Nullable
    public static String seasonLine(ItemStack stack) {
        return root(stack).getString(SEASON);
    }

    @Nullable
    public static String biomeRouteLine(ItemStack stack) {
        return root(stack).getString(BIOME_ROUTE);
    }

    @Nullable
    public static String speciesLine(ItemStack stack) {
        return root(stack).getString(SPECIES);
    }

    public static boolean hasReadout(ItemStack stack) {
        CompoundTag tag = root(stack);
        return tag.contains(SEASON) || tag.contains(BIOME_ROUTE) || tag.contains(SPECIES);
    }

    private static CompoundTag root(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) {
            return new CompoundTag();
        }
        CompoundTag copy = custom.copyTag();
        if (!copy.contains(ROOT)) {
            return new CompoundTag();
        }
        return copy.getCompound(ROOT);
    }

    private static void apply(ItemStack stack, CompoundTag telemetry) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag root = custom != null ? custom.copyTag() : new CompoundTag();
        root.put(ROOT, telemetry);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }
}
