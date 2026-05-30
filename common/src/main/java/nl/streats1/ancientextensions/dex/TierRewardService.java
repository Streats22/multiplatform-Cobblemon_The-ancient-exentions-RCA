package nl.streats1.ancientextensions.dex;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tracks and grants one-time research rank rewards.
 */
public final class TierRewardService {

    private final RegionalSurveyService surveyService;

    public TierRewardService(RegionalSurveyService surveyService) {
        this.surveyService = surveyService;
    }

    public List<ResearchTier> unclaimedTiers(RegionalSurveyData data) {
        ResearchTier current = data.getTier();
        List<ResearchTier> pending = new ArrayList<>();
        for (ResearchTier tier : ResearchTier.values()) {
            if (current.ordinal() >= tier.ordinal() && !data.hasClaimedTierReward(tier)) {
                pending.add(tier);
            }
        }
        return pending;
    }

    public int unclaimedCount(RegionalSurveyData data) {
        return unclaimedTiers(data).size();
    }

    public boolean claim(ServerPlayer player, Optional<ResearchTier> specificTier) {
        RegionalSurveyData data = surveyService.get(player);
        List<ResearchTier> pending = unclaimedTiers(data);
        if (pending.isEmpty()) {
            player.sendSystemMessage(Component.translatable("ancient_extensions.rewards.none_pending"));
            return false;
        }

        List<ResearchTier> toClaim = new ArrayList<>();
        if (specificTier.isPresent()) {
            ResearchTier tier = specificTier.get();
            if (!pending.contains(tier)) {
                player.sendSystemMessage(Component.translatable("ancient_extensions.rewards.not_claimable", tier.displayName()));
                return false;
            }
            toClaim.add(tier);
        } else {
            toClaim.addAll(pending);
        }

        List<ItemStack> granted = new ArrayList<>();
        for (ResearchTier tier : toClaim) {
            data.markTierRewardClaimed(tier);
            granted.addAll(TierRewardTable.rewardsFor(tier));
            player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.rewards.claimed_tier",
                    tier.displayName()
            ));
        }
        surveyService.save(player, data);
        giveItems(player, granted);
        player.sendSystemMessage(Component.translatable("ancient_extensions.rewards.claimed_hint"));
        return true;
    }

    private static void giveItems(ServerPlayer player, List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }
            if (!player.getInventory().add(stack.copy())) {
                player.drop(stack.copy(), false);
            }
        }
    }
}
