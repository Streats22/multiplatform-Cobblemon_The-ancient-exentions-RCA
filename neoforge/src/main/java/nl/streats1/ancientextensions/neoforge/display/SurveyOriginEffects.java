package nl.streats1.ancientextensions.neoforge.display;

import nl.streats1.ancientextensions.dex.PassportInventorySync;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server-side follow-up when a survey origin is registered or changed.
 */
public final class SurveyOriginEffects {

    private SurveyOriginEffects() {
    }

    public static void apply(ServerPlayer player, SurveyRegion region, boolean announce) {
        PassportInventorySync.applyOriginToPassports(player, region);
        RegionPlayerDisplay.refresh(player, announce);
    }
}
