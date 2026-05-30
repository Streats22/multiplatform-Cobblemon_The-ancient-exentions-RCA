package nl.streats1.ancientextensions.client;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.pouch.PouchTierData;
import nl.streats1.ancientextensions.registry.ModContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public final class PokeballPouchClient {

    private PokeballPouchClient() {
    }

    public static void registerItemProperties() {
        ResourceLocation tierProperty = ResourceLocation.fromNamespaceAndPath(
                AncientExtensionsConstants.MOD_ID,
                "pouch_tier"
        );
        ItemProperties.register(
                ModContent.POKEBALL_POUCH,
                tierProperty,
                (stack, level, entity, seed) -> PouchTierData.getTier(stack).ordinal() / 3.0F
        );
    }
}
