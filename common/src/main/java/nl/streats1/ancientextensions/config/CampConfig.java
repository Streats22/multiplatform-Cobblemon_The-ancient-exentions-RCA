package nl.streats1.ancientextensions.config;

/**
 * Runtime camp / Field Kit settings (loaded from platform config on startup).
 */
public final class CampConfig {

    private static boolean starterSuppliesInChestOnly = true;
    private static boolean campBedSetsSpawn = true;
    private static boolean useLootrCampChest = false;

    private CampConfig() {
    }

    public static boolean starterSuppliesInChestOnly() {
        return starterSuppliesInChestOnly;
    }

    public static boolean campBedSetsSpawn() {
        return campBedSetsSpawn;
    }

    public static boolean useLootrCampChest() {
        return useLootrCampChest;
    }

    public static void apply(boolean chestOnlyStarterSupplies, boolean bedSetsSpawn, boolean lootrCampChest) {
        starterSuppliesInChestOnly = chestOnlyStarterSupplies;
        campBedSetsSpawn = bedSetsSpawn;
        useLootrCampChest = lootrCampChest;
    }
}
