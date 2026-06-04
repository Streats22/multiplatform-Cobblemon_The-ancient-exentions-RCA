package nl.streats1.ancientextensions.neoforge.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.recipe.PokeballPouchRecipe;

public final class NeoForgeRecipeSerializers {

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, AncientExtensionsConstants.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<PokeballPouchRecipe>> POKEBALL_POUCH =
            SERIALIZERS.register("pokeball_pouch", () -> new SimpleCraftingRecipeSerializer<>(PokeballPouchRecipe::new));

    private NeoForgeRecipeSerializers() {
    }

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
    }
}
