package com.streats.ancientextensions.dex;

import net.minecraft.server.level.ServerPlayer;

/**
 * Platform hook for multiplayer display and passport sync after origin changes.
 */
public final class SurveyOriginHooks {

    @FunctionalInterface
    public interface Handler {
        void onOriginApplied(ServerPlayer player, SurveyRegion region, boolean announce);
    }

    private static Handler handler = (player, region, announce) -> { };

    private SurveyOriginHooks() {
    }

    public static void setHandler(Handler handler) {
        SurveyOriginHooks.handler = handler;
    }

    public static void notifyApplied(ServerPlayer player, SurveyRegion region, boolean announce) {
        handler.onOriginApplied(player, region, announce);
    }
}
