package nl.streats1.ancientextensions.network;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.dex.ResearchTier;
import nl.streats1.ancientextensions.menu.JournalMenuOpener;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class TierRewardNetworking {

    private TierRewardNetworking() {
    }

    public static void handleClaim(ServerPlayer player, ClaimTierRewardPayload payload) {
        Optional<ResearchTier> tier = Optional.empty();
        if (payload.tierId() != null && !payload.tierId().isBlank()) {
            try {
                tier = Optional.of(ResearchTier.valueOf(payload.tierId()));
            } catch (IllegalArgumentException ignored) {
                return;
            }
        }
        if (AncientExtensionsContext.get().tierRewards().claim(player, tier)) {
            JournalMenuOpener.open(player);
        }
    }
}
