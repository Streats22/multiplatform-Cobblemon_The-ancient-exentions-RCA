package nl.streats1.ancientextensions.neoforge.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.block.FieldSurveyCalendarBlock;
import nl.streats1.ancientextensions.block.FieldSurveyMonitorBlock;
import nl.streats1.ancientextensions.block.FieldSurveySensorBlock;
import nl.streats1.ancientextensions.block.PokeballPouchBlock;
import nl.streats1.ancientextensions.registry.ModContent;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(AncientExtensionsConstants.MOD_ID);

    public static final DeferredBlock<FieldSurveyCalendarBlock> FIELD_SURVEY_CALENDAR = BLOCKS.register(
            "field_survey_calendar",
            () -> new FieldSurveyCalendarBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.6f)
                            .sound(net.minecraft.world.level.block.SoundType.WOOD)
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<FieldSurveySensorBlock> FIELD_SURVEY_SENSOR = BLOCKS.register(
            "field_survey_sensor",
            () -> new FieldSurveySensorBlock(
                    BlockBehaviour.Properties.of()
                            .strength(1.2f)
                            .sound(net.minecraft.world.level.block.SoundType.COPPER)
            )
    );

    public static final DeferredBlock<FieldSurveyMonitorBlock> FIELD_SURVEY_MONITOR = BLOCKS.register(
            "field_survey_monitor",
            () -> new FieldSurveyMonitorBlock(
                    BlockBehaviour.Properties.of()
                            .strength(1.2f)
                            .sound(net.minecraft.world.level.block.SoundType.COPPER)
            )
    );

    public static final DeferredBlock<PokeballPouchBlock> POKEBALL_POUCH = BLOCKS.register(
            "pokeball_pouch",
            () -> new PokeballPouchBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.8f)
                            .sound(net.minecraft.world.level.block.SoundType.WOOL)
                            .noOcclusion()
                            .dynamicShape()
            )
    );

    private ModBlocks() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        modBus.addListener(ModBlocks::onRegister);
    }

    private static void onRegister(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            ModContent.POKEBALL_POUCH_BLOCK = POKEBALL_POUCH.get();
            ModContent.FIELD_SURVEY_SENSOR_BLOCK = FIELD_SURVEY_SENSOR.get();
            ModContent.FIELD_SURVEY_MONITOR_BLOCK = FIELD_SURVEY_MONITOR.get();
            ModContent.FIELD_SURVEY_CALENDAR_BLOCK = FIELD_SURVEY_CALENDAR.get();
        }
    }
}
