package nl.streats1.ancientextensions.neoforge.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.neoforge.integration.jei.AncientExtensionsJeiRecipes;
import nl.streats1.ancientextensions.neoforge.registry.ModItems;
import nl.streats1.ancientextensions.pouch.PouchDisplayStacks;
import nl.streats1.ancientextensions.pouch.PouchTier;
import nl.streats1.ancientextensions.pouch.PouchTierData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        AncientExtensionsJeiRecipes.registerVanillaExtensions(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
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

        registration.addIngredientInfo(
                new ItemStack(ModItems.FIELD_SURVEY_TABLET.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("item.ancient_extensions.field_survey_tablet.description"),
                Component.translatable("ancient_extensions.tablet.tooltip_use")
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        AncientExtensionsJeiRecipes.registerCatalysts(
                registration,
                ModItems.POKEBALL_POUCH.get(),
                ModItems.FIELD_SURVEY_TABLET.get()
        );
    }
}
