package nl.streats1.ancientextensions.neoforge.registry;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.block.PokeballPouchBlock;
import nl.streats1.ancientextensions.block.PokeballPouchBlockEntity;
import nl.streats1.ancientextensions.item.AncientProfessorsKitItem;
import nl.streats1.ancientextensions.item.PokeballPouchItem;
import nl.streats1.ancientextensions.item.RegionalPassportItem;
import nl.streats1.ancientextensions.item.RegionalSurveyJournalItem;
import nl.streats1.ancientextensions.registry.ModContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
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
        modBus.addListener(ModBlocks::onRegister);
    }

    private static void onRegister(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            ModContent.POKEBALL_POUCH_BLOCK = POKEBALL_POUCH.get();
        }
    }
}
