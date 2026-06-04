package nl.streats1.ancientextensions.dex;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.AncientExtensionsContext;

/**
 * Awards Minecraft advancements when players reach Regional Survey research ranks.
 */
public final class ResearchTierAdvancements {

    private static final ResourceLocation ROOT = AncientExtensionsConstants.id("research/tier/root");

    private ResearchTierAdvancements() {
    }

    public static void syncForPlayer(ServerPlayer player, ResearchTier currentTier) {
        if (!shouldSync(player, currentTier)) {
            return;
        }
        for (ResearchTier tier : ResearchTier.values()) {
            if (tier.ordinal() <= currentTier.ordinal()) {
                awardTier(player, tier);
            }
        }
    }

    private static boolean shouldSync(ServerPlayer player, ResearchTier currentTier) {
        if (currentTier.ordinal() > ResearchTier.ROOKIE.ordinal()) {
            return true;
        }
        var data = AncientExtensionsContext.get().surveys().get(player);
        return data.hasDeployedProfessorsKit()
                || data.getCaughtSpeciesCount() > 0
                || data.getResearchPoints() > 0;
    }

    public static void onTierChanged(ServerPlayer player, ResearchTier before, ResearchTier after) {
        if (after.ordinal() <= before.ordinal()) {
            return;
        }
        for (ResearchTier tier : ResearchTier.values()) {
            if (tier.ordinal() > before.ordinal() && tier.ordinal() <= after.ordinal()) {
                awardTier(player, tier);
            }
        }
    }

    private static void awardTier(ServerPlayer player, ResearchTier tier) {
        if (tier == ResearchTier.ROOKIE) {
            awardAdvancement(player, ROOT);
        }
        awardAdvancement(player, tierId(tier));
    }

    private static ResourceLocation tierId(ResearchTier tier) {
        return AncientExtensionsConstants.id("research/tier/" + tier.name().toLowerCase());
    }

    private static void awardAdvancement(ServerPlayer player, ResourceLocation id) {
        AdvancementHolder advancement = player.server.getAdvancements().get(id);
        if (advancement == null) {
            return;
        }
        if (player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            return;
        }
        advancement.value().criteria().keySet().forEach(criterion ->
                player.getAdvancements().award(advancement, criterion)
        );
    }
}
