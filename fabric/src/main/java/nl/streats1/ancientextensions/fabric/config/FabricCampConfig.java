package nl.streats1.ancientextensions.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.streats1.ancientextensions.config.CampConfig;
import nl.streats1.ancientextensions.config.MigrationCalendarConfig;
import nl.streats1.ancientextensions.config.PassportConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FabricCampConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("ancient_extensions");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("ancient_extensions.json");

    private static final boolean DEFAULT_CHEST_ONLY = true;
    private static final boolean DEFAULT_BED_SPAWN = true;
    private static final boolean DEFAULT_LOOTR_CHEST = false;
    private static final boolean DEFAULT_OPEN_ORIGIN_PICKER = true;
    private static final boolean DEFAULT_DEFER_ORIGIN_FOR_MCA = true;
    private static final boolean DEFAULT_USE_SERENE_SEASONS = true;

    private FabricCampConfig() {
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            writeDefault();
        }
        try {
            JsonObject root = JsonParser.parseString(Files.readString(CONFIG_PATH)).getAsJsonObject();
            JsonObject camp = root.has("camp") ? root.getAsJsonObject("camp") : root;
            boolean chestOnly = readBoolean(camp, "starterSuppliesInChestOnly", DEFAULT_CHEST_ONLY);
            boolean bedSpawn = readBoolean(camp, "campBedSetsSpawn", DEFAULT_BED_SPAWN);
            boolean lootrChest = readBoolean(camp, "useLootrCampChest", DEFAULT_LOOTR_CHEST);
            CampConfig.apply(chestOnly, bedSpawn, lootrChest);

            JsonObject passport = root.has("passport") ? root.getAsJsonObject("passport") : new JsonObject();
            boolean openOriginPicker = readBoolean(passport, "openOriginPickerOnJoin", DEFAULT_OPEN_ORIGIN_PICKER);
            boolean deferForMca = readBoolean(passport, "deferOriginPickerForMca", DEFAULT_DEFER_ORIGIN_FOR_MCA);
            PassportConfig.apply(openOriginPicker, deferForMca);

            JsonObject migration = root.has("migration") ? root.getAsJsonObject("migration") : new JsonObject();
            boolean useSereneSeasons = readBoolean(migration, "useSereneSeasonsWhenPresent", DEFAULT_USE_SERENE_SEASONS);
            MigrationCalendarConfig.apply(useSereneSeasons);
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Failed to read config at {}; using defaults", CONFIG_PATH, exception);
            CampConfig.apply(DEFAULT_CHEST_ONLY, DEFAULT_BED_SPAWN, DEFAULT_LOOTR_CHEST);
            PassportConfig.apply(DEFAULT_OPEN_ORIGIN_PICKER, DEFAULT_DEFER_ORIGIN_FOR_MCA);
            MigrationCalendarConfig.apply(DEFAULT_USE_SERENE_SEASONS);
        }
    }

    private static boolean readBoolean(JsonObject object, String key, boolean defaultValue) {
        return object.has(key) ? object.get(key).getAsBoolean() : defaultValue;
    }

    private static void writeDefault() {
        JsonObject camp = new JsonObject();
        camp.addProperty(
                "_comment_starterSuppliesInChestOnly",
                "When true, all Field Kit starter items go in the camp chest instead of the player's inventory."
        );
        camp.addProperty("starterSuppliesInChestOnly", DEFAULT_CHEST_ONLY);
        camp.addProperty(
                "_comment_campBedSetsSpawn",
                "When true, the camp bedroll is a real bed that lets the player set their respawn point at camp (ignored when Comforts is installed)."
        );
        camp.addProperty("campBedSetsSpawn", DEFAULT_BED_SPAWN);
        camp.addProperty(
                "_comment_useLootrCampChest",
                "When true and Lootr is installed, the camp chest becomes a per-player Lootr chest so every player gets their own starter supplies."
        );
        camp.addProperty("useLootrCampChest", DEFAULT_LOOTR_CHEST);

        JsonObject passport = new JsonObject();
        passport.addProperty(
                "_comment_openOriginPickerOnJoin",
                "When true, new players without a stamped origin are prompted with the passport region and hometown picker on join. Set false if your server assigns origins itself."
        );
        passport.addProperty("openOriginPickerOnJoin", DEFAULT_OPEN_ORIGIN_PICKER);
        passport.addProperty(
                "_comment_deferOriginPickerForMca",
                "When true and Minecraft Comes Alive Reborn (mca) is installed, the passport stamp picker opens after MCA's character/destiny intro instead of on join."
        );
        passport.addProperty("deferOriginPickerForMca", DEFAULT_DEFER_ORIGIN_FOR_MCA);

        JsonObject migration = new JsonObject();
        migration.addProperty(
                "_comment_useSereneSeasonsWhenPresent",
                "When true and Serene Seasons (sereneseasons) is installed, migration routes follow the world's real season instead of the 7-day internal calendar."
        );
        migration.addProperty("useSereneSeasonsWhenPresent", DEFAULT_USE_SERENE_SEASONS);

        JsonObject root = new JsonObject();
        root.add("camp", camp);
        root.add("passport", passport);
        root.add("migration", migration);

        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(root));
        } catch (IOException exception) {
            LOGGER.warn("Failed to write default camp config at {}", CONFIG_PATH, exception);
        }
    }
}
