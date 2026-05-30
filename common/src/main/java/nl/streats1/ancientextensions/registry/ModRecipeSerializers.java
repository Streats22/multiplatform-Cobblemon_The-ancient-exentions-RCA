package nl.streats1.ancientextensions.registry;

import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Bound by each platform when recipe serializers register.
 */
public final class ModRecipeSerializers {

    public static RecipeSerializer<?> POKEBALL_POUCH;

    private ModRecipeSerializers() {
    }
}
