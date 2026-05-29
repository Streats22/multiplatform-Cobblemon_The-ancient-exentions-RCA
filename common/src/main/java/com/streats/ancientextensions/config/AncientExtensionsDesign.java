package com.streats.ancientextensions.config;

/**
 * Locked design decisions for Rubius / Ancient Extensions.
 */
public final class AncientExtensionsDesign {

    /** Professor's kit is a custom mod item (camp placed by code), not a structure datapack. */
    public static final boolean KIT_VIA_MOD_ITEM = true;

    /** Regional Survey counts Cobblemon captures only — PC registration is ignored. */
    public static final boolean DEX_CATCH_ONLY = true;

    /** Migration routes reset each in-game season and can be completed again with diminishing RP. */
    public static final boolean MIGRATION_REPEATABLE_PER_SEASON = true;

    /** Migratory species are added via low-weight spawn pools, not biome pool overwrites. */
    public static final boolean MIGRATION_ADDITIVE_SPAWNS = true;

    private AncientExtensionsDesign() {
    }
}
