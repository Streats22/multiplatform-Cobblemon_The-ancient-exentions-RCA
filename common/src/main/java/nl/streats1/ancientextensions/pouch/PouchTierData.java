package nl.streats1.ancientextensions.pouch;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Stores which Poké Ball was used to craft the pouch (texture + capacity tier).
 */
public final class PouchTierData {

    private static final String ROOT = "AncientExtensionsPouch";
    private static final String TIER = "tier";
    private static final String BALL_ID = "ballId";

    private PouchTierData() {
    }

    public static PouchTier getTier(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) {
            return PouchTier.POKE;
        }
        CompoundTag tag = custom.copyTag();
        if (!tag.contains(ROOT)) {
            return PouchTier.POKE;
        }
        CompoundTag pouch = tag.getCompound(ROOT);
        if (pouch.contains(TIER)) {
            return PouchTier.fromName(pouch.getString(TIER));
        }
        if (pouch.contains(BALL_ID)) {
            return PouchTier.fromBallId(ResourceLocation.tryParse(pouch.getString(BALL_ID)));
        }
        return PouchTier.POKE;
    }

    public static ResourceLocation getBallId(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) {
            return defaultBallId(getTier(stack));
        }
        CompoundTag tag = custom.copyTag();
        if (!tag.contains(ROOT)) {
            return defaultBallId(getTier(stack));
        }
        String id = tag.getCompound(ROOT).getString(BALL_ID);
        if (id.isBlank()) {
            return defaultBallId(getTier(stack));
        }
        return ResourceLocation.tryParse(id);
    }

    public static int getSlotCount(ItemStack stack) {
        return getTier(stack).slotCount();
    }

    public static void write(ItemStack pouchStack, ItemStack craftBall) {
        PouchTier tier = PouchTier.fromBall(craftBall);
        ResourceLocation ballId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(craftBall.getItem());

        CompoundTag pouch = new CompoundTag();
        pouch.putString(TIER, tier.getId());
        if (ballId != null) {
            pouch.putString(BALL_ID, ballId.toString());
        }

        CompoundTag root = new CompoundTag();
        root.put(ROOT, pouch);
        pouchStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }

    public static void writeTier(ItemStack pouchStack, PouchTier tier) {
        writeFromStored(pouchStack, tier, defaultBallId(tier));
    }

    public static void writeFromStored(ItemStack pouchStack, PouchTier tier, ResourceLocation ballId) {
        CompoundTag pouch = new CompoundTag();
        pouch.putString(TIER, tier.getId());
        pouch.putString(BALL_ID, ballId.toString());

        CompoundTag root = new CompoundTag();
        root.put(ROOT, pouch);
        pouchStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }

    public static ResourceLocation defaultBallId(PouchTier tier) {
        return switch (tier) {
            case GREAT -> ResourceLocation.fromNamespaceAndPath("cobblemon", "great_ball");
            case ULTRA -> ResourceLocation.fromNamespaceAndPath("cobblemon", "ultra_ball");
            case MASTER -> ResourceLocation.fromNamespaceAndPath("cobblemon", "master_ball");
            default -> ResourceLocation.fromNamespaceAndPath("cobblemon", "poke_ball");
        };
    }
}
