package nl.streats1.ancientextensions.fabric.registry;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.registry.ModCreativeEntries;
import nl.streats1.ancientextensions.registry.ModContent;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
                        .displayItems((params, output) -> ModCreativeEntries.acceptAll(output::accept))
                        .build()
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SEARCH).register(entries -> addVanillaEntries(entries::accept));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> addVanillaEntries(entries::accept));
    }

    private static void addVanillaEntries(java.util.function.Consumer<ItemStack> output) {
        ModCreativeEntries.acceptAll(output);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AncientExtensionsConstants.MOD_ID, path);
    }
}
