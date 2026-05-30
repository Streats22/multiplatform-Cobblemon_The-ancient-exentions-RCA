package nl.streats1.ancientextensions.dex;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * One-time item bundles granted when a player claims a research rank reward.
 * Scales with {@link ResearchTier#ordinal()} so new ranks stay rewarding without hand-tuning every tier.
 */
public final class TierRewardTable {

    public record RewardItem(String itemId, int count) {
        public ItemStack toStack() {
            ResourceLocation id = ResourceLocation.parse(itemId);
            Item item = BuiltInRegistries.ITEM.get(id);
            if (item == Items.AIR) {
                return ItemStack.EMPTY;
            }
            return new ItemStack(item, count);
        }
    }

    private TierRewardTable() {
    }

    public static List<ItemStack> rewardsFor(ResearchTier tier) {
        List<ItemStack> stacks = new ArrayList<>();
        for (RewardItem reward : rewardItems(tier)) {
            ItemStack stack = reward.toStack();
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    private static List<RewardItem> rewardItems(ResearchTier tier) {
        int rank = tier.ordinal();
        List<RewardItem> rewards = new ArrayList<>();

        rewards.add(item("cobblemon:poke_ball", 3 + rank));
        if (rank >= 1) {
            rewards.add(item("cobblemon:potion", 2 + rank / 2));
        }
        if (rank >= 2) {
            rewards.add(item("cobblemon:great_ball", 2 + rank / 2));
        }
        if (rank >= 4) {
            rewards.add(item("cobblemon:super_potion", 2 + rank / 3));
            rewards.add(item("cobblemon:oran_berry", 2 + rank / 4));
        }
        if (rank >= 6) {
            rewards.add(item("cobblemon:revive", 1 + rank / 5));
        }
        if (rank >= 8) {
            rewards.add(item("cobblemon:ultra_ball", 1 + rank / 4));
        }
        if (rank >= 10) {
            rewards.add(item("cobblemon:hyper_potion", 2 + rank / 5));
        }
        if (rank >= 12) {
            rewards.add(item("cobblemon:exp_candy_xs", 2 + rank / 3));
        }
        if (rank >= 14) {
            rewards.add(item("cobblemon:max_potion", 1 + rank / 6));
            rewards.add(item("cobblemon:full_heal", 1 + rank / 8));
        }
        if (rank >= 16) {
            rewards.add(item("cobblemon:exp_candy_s", 1 + rank / 4));
        }
        if (rank >= 18) {
            rewards.add(item("cobblemon:exp_candy_m", 1 + rank / 5));
        }
        if (rank >= 19) {
            rewards.add(item("cobblemon:full_restore", 1 + rank / 10));
        }
        if (tier == ResearchTier.LEGENDARY_AUTHORITY) {
            rewards.add(item("cobblemon:exp_candy_l", 2));
            rewards.add(item("cobblemon:ultra_ball", 8));
        }

        return rewards;
    }

    private static RewardItem item(String itemId, int count) {
        return new RewardItem(itemId, Math.max(1, count));
    }
}
