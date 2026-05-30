package nl.streats1.ancientextensions.neoforge.config;

import nl.streats1.ancientextensions.config.CampConfig;
import nl.streats1.ancientextensions.config.PassportConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class NeoForgeCampConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue STARTER_SUPPLIES_IN_CHEST_ONLY;
    public static final ModConfigSpec.BooleanValue CAMP_BED_SETS_SPAWN;
    public static final ModConfigSpec.BooleanValue USE_LOOTR_CAMP_CHEST;
    public static final ModConfigSpec.BooleanValue OPEN_ORIGIN_PICKER_ON_JOIN;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("camp");
        STARTER_SUPPLIES_IN_CHEST_ONLY = BUILDER
                .comment("When true, all Field Kit starter items go in the camp chest instead of the player's inventory.")
                .define("starterSuppliesInChestOnly", true);
        CAMP_BED_SETS_SPAWN = BUILDER
                .comment("When true, the camp bedroll is a real bed that lets the player set their respawn point at camp (ignored when Comforts is installed).")
                .define("campBedSetsSpawn", true);
        USE_LOOTR_CAMP_CHEST = BUILDER
                .comment("When true and Lootr is installed, the camp chest becomes a per-player Lootr chest so every player gets their own starter supplies.")
                .define("useLootrCampChest", false);
        BUILDER.pop();
        BUILDER.push("passport");
        OPEN_ORIGIN_PICKER_ON_JOIN = BUILDER
                .comment("When true, new players without a stamped origin are prompted with the passport region and hometown picker on join. Set false if your server assigns origins itself.")
                .define("openOriginPickerOnJoin", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private NeoForgeCampConfig() {
    }

    public static void sync() {
        boolean chestOnly = STARTER_SUPPLIES_IN_CHEST_ONLY.get();
        boolean bedSetsSpawn = CAMP_BED_SETS_SPAWN.get();
        boolean lootrChest = USE_LOOTR_CAMP_CHEST.get();
        boolean openOriginPicker = OPEN_ORIGIN_PICKER_ON_JOIN.get();
        CampConfig.apply(chestOnly, bedSetsSpawn, lootrChest);
        PassportConfig.apply(openOriginPicker);
    }
}
