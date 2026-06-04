package nl.streats1.ancientextensions.fabric.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import nl.streats1.ancientextensions.pouch.PouchDisplayStacks;
import nl.streats1.ancientextensions.pouch.PouchTier;
import nl.streats1.ancientextensions.recipe.PokeballPouchRecipe;
import nl.streats1.ancientextensions.registry.ModContent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

/** Teaches JEI how to draw the real {@link PokeballPouchRecipe} crafting grid. */
public final class PokeballPouchCraftingExtension implements ICraftingCategoryExtension<PokeballPouchRecipe> {

    private static final int WIDTH = 3;
    private static final int HEIGHT = 3;

    @Override
    public void setRecipe(
            RecipeHolder<PokeballPouchRecipe> recipeHolder,
            IRecipeLayoutBuilder builder,
            ICraftingGridHelper craftingGridHelper,
            IFocusGroup focuses
    ) {
        List<ItemStack> outputs = new ArrayList<>();
        for (PouchTier tier : PouchTier.values()) {
            outputs.add(PouchDisplayStacks.tierSample(ModContent.POKEBALL_POUCH, tier));
        }

        craftingGridHelper.createAndSetOutputs(builder, outputs);
        craftingGridHelper.createAndSetIngredients(
                builder,
                recipeHolder.value().jeiIngredients(),
                WIDTH,
                HEIGHT
        );
    }

    @Override
    public int getWidth(RecipeHolder<PokeballPouchRecipe> recipeHolder) {
        return WIDTH;
    }

    @Override
    public int getHeight(RecipeHolder<PokeballPouchRecipe> recipeHolder) {
        return HEIGHT;
    }
}
