package nl.streats1.ancientextensions.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Optional;

/** Persists migration compass bearing target on the item stack. */
public final class CompassTargetData {

    private static final String KEY_ACTIVE = "Active";
    private static final String KEY_POS = "Pos";
    private static final String KEY_BIOME = "Biome";

    private CompassTargetData() {
    }

    public record Target(BlockPos position, String biomeLabel, boolean active) {
    }

    public static void write(ItemStack stack, BlockPos position, String biomeLabel) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(KEY_ACTIVE, true);
        tag.putLong(KEY_POS, position.asLong());
        tag.putString(KEY_BIOME, biomeLabel);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void clear(ItemStack stack) {
        stack.remove(DataComponents.CUSTOM_DATA);
    }

    public static Optional<Target> read(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) {
            return Optional.empty();
        }
        CompoundTag tag = custom.copyTag();
        if (!tag.getBoolean(KEY_ACTIVE) || !tag.contains(KEY_POS)) {
            return Optional.empty();
        }
        return Optional.of(new Target(
                BlockPos.of(tag.getLong(KEY_POS)),
                tag.getString(KEY_BIOME),
                true
        ));
    }
}
