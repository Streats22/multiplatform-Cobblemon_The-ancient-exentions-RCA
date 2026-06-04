package nl.streats1.ancientextensions.dex;

import net.minecraft.network.chat.Component;

/**
 * Research ranks earned from catch-only Regional Survey points (RP).
 * Anchor ranks (Surveyor, Naturalist, League Researcher, Regional Authority) keep their original RP gates.
 */
public enum ResearchTier {
    ROOKIE(0, 12),
    FIELD_AIDE(13, 24),
    SURVEYOR(25, 39),
    JUNIOR_TRACKER(40, 54),
    TRACKER(55, 74),
    NATURALIST(75, 99),
    FIELD_NATURALIST(100, 119),
    ECOLOGIST(120, 149),
    ROUTE_SCOUT(150, 174),
    MIGRATION_SPECIALIST(175, 199),
    LEAGUE_AIDE(200, 229),
    LEAGUE_RESEARCHER(230, 269),
    SENIOR_RESEARCHER(270, 299),
    REGIONAL_AUTHORITY(300, 339),
    BIOME_EXPERT(340, 379),
    REGIONAL_ANALYST(380, 424),
    CHIEF_RESEARCHER(425, 474),
    CONTINENTAL_SURVEYOR(475, 529),
    MASTER_SURVEYOR(530, 599),
    GRAND_AUTHORITY(600, 699),
    LEGENDARY_AUTHORITY(700, Integer.MAX_VALUE);

    private static final ResearchTier MAX_TIER = values()[values().length - 1];

    private final int minPoints;
    private final int maxPoints;

    ResearchTier(int minPoints, int maxPoints) {
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
    }

    public static ResearchTier fromPoints(int points) {
        for (ResearchTier tier : values()) {
            if (points >= tier.minPoints && points <= tier.maxPoints) {
                return tier;
            }
        }
        return MAX_TIER;
    }

    public static ResearchTier maxTier() {
        return MAX_TIER;
    }

    public Component displayName() {
        return Component.translatable("ancient_extensions.dex.tier." + name().toLowerCase());
    }

    public int minPoints() {
        return minPoints;
    }

    public int maxPoints() {
        return maxPoints;
    }

    /**
     * RP still needed to reach the next rank, or {@code 0} at max rank.
     */
    public int pointsToNext(int currentPoints) {
        ResearchTier[] tiers = values();
        for (int i = 0; i < tiers.length - 1; i++) {
            if (tiers[i] == this) {
                return Math.max(0, tiers[i + 1].minPoints() - currentPoints);
            }
        }
        return 0;
    }

    public ResearchTier nextTier() {
        int next = ordinal() + 1;
        return next < values().length ? values()[next] : null;
    }
}
