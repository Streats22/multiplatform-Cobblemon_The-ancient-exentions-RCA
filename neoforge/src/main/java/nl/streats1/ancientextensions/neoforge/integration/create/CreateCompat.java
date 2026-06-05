package nl.streats1.ancientextensions.neoforge.integration.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.field.FieldSurveyPower;
import nl.streats1.ancientextensions.integration.OptionalIntegrationMods;
import nl.streats1.ancientextensions.neoforge.registry.ModBlockEntities;
import nl.streats1.ancientextensions.neoforge.registry.ModBlocks;

public final class CreateCompat {

    private static boolean displaySourcesRegistered;
    private static boolean displayTargetRegistered;
    private static boolean blocksAvailable;
    private static boolean blocksBound;

    private CreateCompat() {
    }

    public static void register(IEventBus modBus) {
        if (!OptionalIntegrationMods.hasCreate()) {
            return;
        }
        modBus.addListener(CreateCompat::onRegister);
        modBus.addListener(CreateCompat::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FieldSurveyPower.register(FieldSurveyKineticRequirements::hasShaftPowerFromBelow);
            ensureBlocksBound();
        });
    }

    public static void ensureBlocksBound() {
        tryBindBlocks();
    }

    private static void onRegister(RegisterEvent event) {
        if (!OptionalIntegrationMods.hasCreate()) {
            return;
        }

        if (event.getRegistryKey().equals(CreateRegistries.DISPLAY_SOURCE)) {
            registerDisplaySources();
        } else if (event.getRegistryKey().equals(CreateRegistries.DISPLAY_TARGET)) {
            registerDisplayTarget();
        } else if (event.getRegistryKey().equals(Registries.BLOCK)) {
            blocksAvailable = true;
            tryBindBlocks();
        }
    }

    private static void registerDisplaySources() {
        if (displaySourcesRegistered) {
            return;
        }
        registerDisplaySource("field_migration_season", FieldMigrationSeasonDisplaySource.INSTANCE);
        registerDisplaySource("field_biome_route", FieldBiomeRouteDisplaySource.INSTANCE);
        registerDisplaySource("field_migratory_species", FieldMigratorySpeciesDisplaySource.INSTANCE);
        registerDisplaySource("field_route_bearing", FieldRouteBearingDisplaySource.INSTANCE);
        displaySourcesRegistered = true;
        tryBindBlocks();
    }

    private static void registerDisplayTarget() {
        if (displayTargetRegistered) {
            return;
        }
        Registry.register(
                CreateBuiltInRegistries.DISPLAY_TARGET,
                AncientExtensionsConstants.id("field_survey_monitor"),
                FieldSurveyMonitorDisplayTarget.INSTANCE
        );
        displayTargetRegistered = true;
        tryBindBlocks();
    }

    private static void tryBindBlocks() {
        if (blocksBound || !displaySourcesRegistered || !displayTargetRegistered || !blocksAvailable) {
            return;
        }
        Block sensor = ModBlocks.FIELD_SURVEY_SENSOR.get();
        Block monitor = ModBlocks.FIELD_SURVEY_MONITOR.get();
        if (sensor == null || monitor == null) {
            return;
        }

        DisplaySource.BY_BLOCK.add(sensor, FieldMigrationSeasonDisplaySource.INSTANCE);
        DisplaySource.BY_BLOCK.add(sensor, FieldBiomeRouteDisplaySource.INSTANCE);
        DisplaySource.BY_BLOCK.add(sensor, FieldMigratorySpeciesDisplaySource.INSTANCE);
        DisplaySource.BY_BLOCK.add(sensor, FieldRouteBearingDisplaySource.INSTANCE);
        DisplayTarget.BY_BLOCK.register(monitor, FieldSurveyMonitorDisplayTarget.INSTANCE);
        BlockEntityType<?> monitorBe = ModBlockEntities.FIELD_SURVEY_MONITOR.get();
        if (monitorBe != null) {
            DisplayTarget.BY_BLOCK_ENTITY.register(monitorBe, FieldSurveyMonitorDisplayTarget.INSTANCE);
        }
        blocksBound = true;
    }

    private static void registerDisplaySource(String path, DisplaySource source) {
        Registry.register(CreateBuiltInRegistries.DISPLAY_SOURCE, AncientExtensionsConstants.id(path), source);
    }
}
