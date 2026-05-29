package com.streats.ancientextensions.dex;

import net.minecraft.network.chat.Component;

public enum ResearchTier {
    ROOKIE(0, 24),
    SURVEYOR(25, 74),
    NATURALIST(75, 149),
    LEAGUE_RESEARCHER(150, 299),
    REGIONAL_AUTHORITY(300, Integer.MAX_VALUE);

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
        return REGIONAL_AUTHORITY;
    }

    public Component displayName() {
        return Component.translatable("ancient_extensions.dex.tier." + name().toLowerCase());
    }

    public int minPoints() {
        return minPoints;
    }
}
