package nl.streats1.ancientextensions;

import net.minecraft.resources.ResourceLocation;

public final class AncientExtensionsConstants {

    public static final String MOD_ID = "ancient_extensions";

    private AncientExtensionsConstants() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
