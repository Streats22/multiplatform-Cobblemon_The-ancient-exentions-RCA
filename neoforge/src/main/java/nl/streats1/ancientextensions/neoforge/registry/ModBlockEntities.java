package nl.streats1.ancientextensions.neoforge.registry;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.neoforge.block.PokeballPouchBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AncientExtensionsConstants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PokeballPouchBlockEntity>> POKEBALL_POUCH =
            BLOCK_ENTITIES.register("pokeball_pouch", () -> BlockEntityType.Builder.of(
                    PokeballPouchBlockEntity::new,
                    ModBlocks.POKEBALL_POUCH.get()
            ).build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITIES.register(modBus);
    }
}
