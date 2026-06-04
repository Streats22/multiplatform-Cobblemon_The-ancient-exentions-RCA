package nl.streats1.ancientextensions.neoforge.registry;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.block.FieldSurveyMonitorBlockEntity;
import nl.streats1.ancientextensions.block.PokeballPouchBlockEntity;
import nl.streats1.ancientextensions.registry.ModContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AncientExtensionsConstants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FieldSurveyMonitorBlockEntity>> FIELD_SURVEY_MONITOR =
            BLOCK_ENTITIES.register("field_survey_monitor", () -> BlockEntityType.Builder.of(
                    FieldSurveyMonitorBlockEntity::new,
                    ModBlocks.FIELD_SURVEY_MONITOR.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PokeballPouchBlockEntity>> POKEBALL_POUCH =
            BLOCK_ENTITIES.register("pokeball_pouch", () -> BlockEntityType.Builder.of(
                    PokeballPouchBlockEntity::new,
                    ModBlocks.POKEBALL_POUCH.get()
            ).build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITIES.register(modBus);
        modBus.addListener(ModBlockEntities::onRegister);
    }

    private static void onRegister(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
            ModContent.POKEBALL_POUCH_BE = POKEBALL_POUCH.get();
            ModContent.FIELD_SURVEY_MONITOR_BE = FIELD_SURVEY_MONITOR.get();
        }
    }
}
