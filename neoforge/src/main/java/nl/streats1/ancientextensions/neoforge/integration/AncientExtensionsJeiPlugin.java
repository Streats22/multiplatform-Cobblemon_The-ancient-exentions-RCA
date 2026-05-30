package nl.streats1.ancientextensions.neoforge.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.neoforge.registry.ModItems;
import nl.streats1.ancientextensions.pouch.PouchDisplayStacks;
import nl.streats1.ancientextensions.pouch.PouchTier;
import nl.streats1.ancientextensions.pouch.PouchTierData;
import nl.streats1.ancientextensions.registry.ModRecipeSerializers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class AncientExtensionsJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(AncientExtensionsConstants.MOD_ID, "jei");
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        List<ItemStack> tierStacks = new ArrayList<>();
        for (PouchTier tier : PouchTier.values()) {
            tierStacks.add(PouchDisplayStacks.tierSample(ModItems.POKEBALL_POUCH.get(), tier));
        }
        registration.addExtraItemStacks(tierStacks);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(
                ModItems.POKEBALL_POUCH.get(),
                new ISubtypeInterpreter<>() {
                    @Override
                    public Object getSubtypeData(ItemStack stack, UidContext context) {
                        return PouchTierData.getTier(stack).getId() + ":" + PouchTierData.getBallId(stack);
                    }

                    @Override
                    @SuppressWarnings("deprecation")
                    public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
                        return "";
                    }
                }
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        List<RecipeHolder<CraftingRecipe>> pouchRecipes = level.getRecipeManager()
                .getAllRecipesFor(RecipeType.CRAFTING)
                .stream()
                .filter(holder -> holder.value().getSerializer() == ModRecipeSerializers.POKEBALL_POUCH)
                .toList();
        registration.addRecipes(mezz.jei.api.constants.RecipeTypes.CRAFTING, pouchRecipes);

        List<ItemStack> exampleOutputs = new ArrayList<>();
        for (PouchTier tier : PouchTier.values()) {
            exampleOutputs.add(PouchDisplayStacks.tierSample(ModItems.POKEBALL_POUCH.get(), tier));
        }

        ItemStack pokeBall = new ItemStack(
                BuiltInRegistries.ITEM.get(PouchTierData.defaultBallId(PouchTier.POKE))
        );

        registration.addIngredientInfo(
                exampleOutputs,
                VanillaTypes.ITEM_STACK,
                Component.translatable("ancient_extensions.jei.pokeball_pouch.recipe"),
                Component.translatable("ancient_extensions.jei.pokeball_pouch.tiers")
        );

        registration.addIngredientInfo(
                pokeBall,
                VanillaTypes.ITEM_STACK,
                Component.translatable("ancient_extensions.jei.pokeball_pouch.center_ball")
        );

        registration.addIngredientInfo(
                new ItemStack(Items.LEATHER),
                VanillaTypes.ITEM_STACK,
                Component.translatable("ancient_extensions.jei.pokeball_pouch.leather_frame")
        );

        registration.addIngredientInfo(
                new ItemStack(Items.STRING),
                VanillaTypes.ITEM_STACK,
                Component.translatable("ancient_extensions.jei.pokeball_pouch.strap")
        );
    }
}
