package mezz.jei.api.registration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

/**
 * Tell JEI how to interpret Components and capabilities when comparing and looking up ingredients.
 * <p>
 * If your ingredient has subtypes that depend on Components or capabilities,
 * use this so JEI can tell those subtypes apart.
 */
public interface ISubtypeRegistration {

    /**
     * Add an interpreter to allow JEI to understand the differences between ingredient subtypes.
     * This interpreter should account for Components and anything else
     * that's relevant to differentiating the ingredient's subtypes.
     *
     * @param type        the ingredient type (for example {@link VanillaTypes#ITEM_STACK}
     * @param base        the base of the ingredient that has subtypes (for example, {@link class_1802#field_8598}).
     *                    All ingredients with this base will use the given interpreter.
     * @param interpreter the interpreter for the ingredient's subtypes
     * @since 19.9.0
     */
    <B, I> void registerSubtypeInterpreter(IIngredientTypeWithSubtypes<B, I> type, B base, ISubtypeInterpreter<I> interpreter);

    /**
     * Add an interpreter to allow JEI to understand the differences between ingredient subtypes.
     * This interpreter should account for Components and anything else
     * that's relevant to differentiating the ingredient's subtypes.
     *
     * @param item        the item base of the ItemStack that has subtypes (for example, {@link class_1802#field_8598}).
     *                    All ItemStacks with this base will use the given interpreter.
     * @param interpreter the interpreter for the ItemStack's subtypes
     * @since 19.9.0
     */
    default void registerSubtypeInterpreter(class_1792 item, ISubtypeInterpreter<class_1799> interpreter) {
        registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, interpreter);
    }

    /**
     * Add an interpreter to allow JEI to understand the differences between ingredient subtypes.
     * This interpreter should account for Components and anything else
     * that's relevant to differentiating the ingredient's subtypes.
     *
     * @param type        the ingredient type (for example {@link VanillaTypes#ITEM_STACK}
     * @param base        the base of the ingredient that has subtypes (for example, {@link class_1802#field_8598}).
     *                    All ingredients with this base will use the given interpreter.
     * @param interpreter the interpreter for the ingredient's subtypes
     * @since 9.7.0
     * @deprecated use {@link #registerSubtypeInterpreter(IIngredientTypeWithSubtypes, Object, ISubtypeInterpreter)}
     */
    @SuppressWarnings("removal")
    @Deprecated(since = "19.9.0", forRemoval = true)
    <B, I> void registerSubtypeInterpreter(IIngredientTypeWithSubtypes<B, I> type, B base, mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter<I> interpreter);

    /**
     * Add an interpreter to allow JEI to understand the differences between ingredient subtypes.
     * This interpreter should account for Components and anything else
     * that's relevant to differentiating the ingredient's subtypes.
     *
     * @param item        the item base of the ItemStack that has subtypes (for example, {@link class_1802#field_8598}).
     *                    All ItemStacks with this base will use the given interpreter.
     * @param interpreter the interpreter for the ItemStack's subtypes
     * @since 11.1.1
     * @deprecated use {@link #registerSubtypeInterpreter(class_1792, ISubtypeInterpreter)}
     */
    @SuppressWarnings("removal")
    @Deprecated(since = "19.9.0", forRemoval = true)
    default void registerSubtypeInterpreter(class_1792 item, mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter<class_1799> interpreter) {
        registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, interpreter);
    }

}
