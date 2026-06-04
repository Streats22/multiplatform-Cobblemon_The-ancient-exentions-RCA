package nl.streats1.ancientextensions.neoforge.integration.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.integration.OptionalIntegrationMods;
import nl.streats1.ancientextensions.registry.ModContent;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

public final class CreateCompat {

    private static boolean registered;

    private CreateCompat() {
    }

    public static void init() {
        if (registered || !OptionalIntegrationMods.hasCreate()) {
            return;
        }
        Block sensor = ModContent.FIELD_SURVEY_SENSOR_BLOCK;
        Block monitor = ModContent.FIELD_SURVEY_MONITOR_BLOCK;
        if (sensor == null || monitor == null) {
            return;
        }

        registerDisplaySource("field_migration_season", FieldMigrationSeasonDisplaySource.INSTANCE);
        registerDisplaySource("field_biome_route", FieldBiomeRouteDisplaySource.INSTANCE);
        registerDisplaySource("field_migratory_species", FieldMigratorySpeciesDisplaySource.INSTANCE);
        registerDisplaySource("field_route_bearing", FieldRouteBearingDisplaySource.INSTANCE);

        DisplaySource.BY_BLOCK.add(sensor, FieldMigrationSeasonDisplaySource.INSTANCE);
        DisplaySource.BY_BLOCK.add(sensor, FieldBiomeRouteDisplaySource.INSTANCE);
        DisplaySource.BY_BLOCK.add(sensor, FieldMigratorySpeciesDisplaySource.INSTANCE);
        DisplaySource.BY_BLOCK.add(sensor, FieldRouteBearingDisplaySource.INSTANCE);

        Registry.register(
                CreateBuiltInRegistries.DISPLAY_TARGET,
                AncientExtensionsConstants.id("field_survey_monitor"),
                FieldSurveyMonitorDisplayTarget.INSTANCE
        );
        DisplayTarget.BY_BLOCK.register(monitor, FieldSurveyMonitorDisplayTarget.INSTANCE);

        registered = true;
    }

    private static void registerDisplaySource(String path, DisplaySource source) {
        Registry.register(CreateBuiltInRegistries.DISPLAY_SOURCE, AncientExtensionsConstants.id(path), source);
    }
}
