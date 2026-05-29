package com.streats.ancientextensions.migration;

public final class MigrationConfig {

    /** RP granted when a full seasonal route is completed (before diminishing). */
    public static final int BASE_ROUTE_COMPLETION_RP = 50;

    /** Multiplier applied for each prior completion of the same season's route. */
    public static final double DIMINISHING_FACTOR = 0.65;

    /** Floor for diminished route completion RP. */
    public static final int MIN_ROUTE_COMPLETION_RP = 8;

    /** In-game days per migration season before the calendar advances. */
    public static final int DAYS_PER_SEASON = 7;

    private MigrationConfig() {
    }

    public static int routeCompletionReward(int priorCompletionsThisSeason) {
        double multiplier = Math.pow(DIMINISHING_FACTOR, priorCompletionsThisSeason);
        int reward = (int) Math.round(BASE_ROUTE_COMPLETION_RP * multiplier);
        return Math.max(MIN_ROUTE_COMPLETION_RP, reward);
    }
}
