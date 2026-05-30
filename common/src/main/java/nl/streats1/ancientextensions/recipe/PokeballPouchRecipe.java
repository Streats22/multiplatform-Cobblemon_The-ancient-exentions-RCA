package nl.streats1.ancientextensions.recipe;

import nl.streats1.ancientextensions.pouch.PokeballFilter;
import nl.streats1.ancientextensions.pouch.PouchTierData;
import nl.streats1.ancientextensions.registry.ModContent;
import nl.streats1.ancientextensions.registry.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Craft with any Poké Ball in the center — tier and look follow the ball used.
 * <pre>
 *  LSL
 *  LBL
 * </pre>
 */
public class PokeballPouchRecipe extends CustomRecipe {

    public PokeballPouchRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() < 3 || input.height() < 3) {
            return false;
        }
        return isLeather(input, 0, 0)
                && isString(input, 1, 0)
                && isLeather(input, 2, 0)
                && isLeather(input, 0, 1)
                && isPokeball(input, 1, 1)
                && isLeather(input, 2, 1)
                && isEmpty(input, 0, 2)
                && isLeather(input, 1, 2)
                && isEmpty(input, 2, 2);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack ball = getBall(input);
        ItemStack result = new ItemStack(ModContent.POKEBALL_POUCH);
        PouchTierData.write(result, ball);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(ModContent.POKEBALL_POUCH);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.POKEBALL_POUCH;
    }

    private static ItemStack getBall(CraftingInput input) {
        return input.getItem(1, 1);
    }

    private static boolean isLeather(CraftingInput input, int x, int y) {
        return input.getItem(x, y).is(Items.LEATHER);
    }

    private static boolean isString(CraftingInput input, int x, int y) {
        return input.getItem(x, y).is(Items.STRING);
    }

    private static boolean isPokeball(CraftingInput input, int x, int y) {
        return PokeballFilter.isPokeball(input.getItem(x, y));
    }

    private static boolean isEmpty(CraftingInput input, int x, int y) {
        return input.getItem(x, y).isEmpty();
    }
}
