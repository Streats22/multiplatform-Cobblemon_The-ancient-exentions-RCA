package nl.streats1.ancientextensions.fabric.integration.jei;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import nl.streats1.ancientextensions.recipe.PokeballPouchRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** JEI hooks for Ancient Extensions crafting recipes (Fabric). */
public final class AncientExtensionsJeiRecipes {

    private AncientExtensionsJeiRecipes() {
    }

    public static void registerVanillaExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(
                PokeballPouchRecipe.class,
                new PokeballPouchCraftingExtension()
        );
    }

    public static void registerCatalysts(IRecipeCatalystRegistration registration, Item pouchItem, Item tabletItem) {
        registration.addRecipeCatalysts(
                RecipeTypes.CRAFTING,
                new ItemStack(Items.CRAFTING_TABLE),
                new ItemStack(pouchItem),
                new ItemStack(tabletItem)
        );
    }
}
