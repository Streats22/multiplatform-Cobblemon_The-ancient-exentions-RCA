package nl.streats1.ancientextensions.neoforge.registry;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.pouch.PouchDisplayStacks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AncientExtensionsConstants.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ancient_extensions"))
                    .icon(() -> new ItemStack(ModItems.ANCIENT_PROFESSORS_KIT.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.ANCIENT_PROFESSORS_KIT.get());
                        output.accept(ModItems.REGIONAL_SURVEY_JOURNAL.get());
                        output.accept(ModItems.REGIONAL_PASSPORT.get());
                        PouchDisplayStacks.acceptAllTiers(output::accept, ModItems.POKEBALL_POUCH.get());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
        modBus.addListener(ModCreativeTabs::addToVanillaTabs);
    }

    private static void addToVanillaTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SEARCH || event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.ANCIENT_PROFESSORS_KIT);
            event.accept(ModItems.REGIONAL_SURVEY_JOURNAL);
            event.accept(ModItems.REGIONAL_PASSPORT);
            PouchDisplayStacks.acceptAllTiers(event::accept, ModItems.POKEBALL_POUCH.get());
        }
    }
}
