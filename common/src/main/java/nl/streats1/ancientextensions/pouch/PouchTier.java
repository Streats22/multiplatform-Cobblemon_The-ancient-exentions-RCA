package nl.streats1.ancientextensions.pouch;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

/**
 * Pouch capacity tier — grows when crafted with Great, Ultra, or Master Balls.
 */
public enum PouchTier implements StringRepresentable {
    POKE("poke", 18),
    GREAT("great", 27),
    ULTRA("ultra", 36),
    MASTER("master", 54);

    private final String id;
    private final int slotCount;

    PouchTier(String id, int slotCount) {
        this.id = id;
        this.slotCount = slotCount;
    }

    public String getId() {
        return id;
    }

    public int slotCount() {
        return slotCount;
    }

    public int rows() {
        return (slotCount + 8) / 9;
    }

    public String textureSuffix() {
        return id;
    }

    @Override
    public String getSerializedName() {
        return id;
    }

    public static PouchTier fromBall(ItemStack ball) {
        if (ball.isEmpty()) {
            return POKE;
        }
        ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(ball.getItem());
        return fromBallId(itemId);
    }

    public static PouchTier fromBallId(ResourceLocation itemId) {
        if (itemId == null) {
            return POKE;
        }
        String path = itemId.getPath();
        if (path.equals("master_ball")) {
            return MASTER;
        }
        if (path.equals("ultra_ball")) {
            return ULTRA;
        }
        if (path.equals("great_ball")) {
            return GREAT;
        }
        return POKE;
    }

    public static PouchTier fromName(String name) {
        if (name == null || name.isBlank()) {
            return POKE;
        }
        try {
            return valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            for (PouchTier tier : values()) {
                if (tier.id.equalsIgnoreCase(name)) {
                    return tier;
                }
            }
            return POKE;
        }
    }
}
