package nl.streats1.ancientextensions.neoforge.registry;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.neoforge.block.PokeballPouchBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(AncientExtensionsConstants.MOD_ID);

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
    }
}
