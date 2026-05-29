package com.streats.ancientextensions.migration;

import net.minecraft.network.chat.Component;

/**
 * Logical seasons for migration routes. Game season detection is wired on NeoForge (Serene Seasons optional).
 */
public enum MigrationSeason {
    SPRING("spring"),
    SUMMER("summer"),
    AUTUMN("autumn"),
    WINTER("winter");

    private final String id;

    MigrationSeason(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Component displayName() {
        return Component.translatable("ancient_extensions.migration.season." + id);
    }
}
