package com.streats.ancientextensions.neoforge.display;

import com.streats.ancientextensions.dex.PassportInventorySync;
import com.streats.ancientextensions.dex.SurveyRegion;
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
