package nl.streats1.ancientextensions.dex;

/**
 * Controls whether an origin change is allowed when one is already set.
 */
public enum OriginChangePolicy {
    /** Reject if the player already chose an origin (passport UI). */
    FIRST_CHOICE,
    /** Always apply — admin commands only. */
    ADMIN_OVERRIDE
}
