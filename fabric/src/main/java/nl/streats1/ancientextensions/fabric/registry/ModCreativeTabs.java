package nl.streats1.ancientextensions.fabric.registry;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.pouch.PouchDisplayStacks;
import nl.streats1.ancientextensions.registry.ModContent;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {

    private ModCreativeTabs() {
    }

    public static void register() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                id("main"),
                FabricItemGroup.builder()
                        .title(Component.translatable("itemGroup.ancient_extensions"))
                        .icon(() -> new ItemStack(ModContent.ANCIENT_PROFESSORS_KIT))
                        .displayItems((params, output) -> {
                            output.accept(ModContent.ANCIENT_PROFESSORS_KIT);
                            output.accept(ModContent.REGIONAL_SURVEY_JOURNAL);
                            output.accept(ModContent.REGIONAL_PASSPORT);
                            PouchDisplayStacks.acceptAllTiers(output::accept, ModContent.POKEBALL_POUCH);
                        })
                        .build()
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SEARCH).register(entries -> addVanillaEntries(entries::accept));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> addVanillaEntries(entries::accept));
    }

    private static void addVanillaEntries(java.util.function.Consumer<ItemStack> output) {
        output.accept(new ItemStack(ModContent.ANCIENT_PROFESSORS_KIT));
        output.accept(new ItemStack(ModContent.REGIONAL_SURVEY_JOURNAL));
        output.accept(new ItemStack(ModContent.REGIONAL_PASSPORT));
        PouchDisplayStacks.acceptAllTiers(output, ModContent.POKEBALL_POUCH);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AncientExtensionsConstants.MOD_ID, path);
    }
}
