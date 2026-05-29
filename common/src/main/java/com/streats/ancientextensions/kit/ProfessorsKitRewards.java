package com.streats.ancientextensions.kit;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * Starter supplies for the Ancient Professor's Field Kit (Rubius pack tuning).
 */
public final class ProfessorsKitRewards {

    public static final int POKE_BALL_COUNT = 10;
    public static final int POTION_COUNT = 5;
    public static final int REVIVE_COUNT = 2;
    public static final int ANTIDOTE_COUNT = 2;
    public static final int GREAT_BALL_COUNT = 3;

    private ProfessorsKitRewards() {
    }

    /** Items given directly to the player. */
    public static List<ItemStack> createPlayerStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(stack("cobblemon:poke_ball", POKE_BALL_COUNT));
        stacks.add(stack("cobblemon:great_ball", GREAT_BALL_COUNT));
        stacks.add(stack("cobblemon:potion", POTION_COUNT));
        stacks.add(stack("cobblemon:revive", REVIVE_COUNT));
        stacks.add(stack("cobblemon:antidote", ANTIDOTE_COUNT));
        stacks.add(stack("cobblemon:ancient_azure_ball", 2));
        stacks.add(stack("cobblemon:oran_berry", 5));
        stacks.add(SurveyFieldNotes.create());
        return stacks;
    }

    /** Backup supplies stored in the camp chest. */
    public static List<ItemStack> createChestStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(stack("cobblemon:poke_ball", 5));
        stacks.add(stack("cobblemon:potion", 3));
        stacks.add(stack("cobblemon:paralyze_heal", 2));
        stacks.add(stack("cobblemon:awakening", 2));
        stacks.add(new ItemStack(Items.BREAD, 8));
        stacks.add(new ItemStack(Items.TORCH, 16));
        stacks.add(new ItemStack(Items.OAK_BOAT, 1));
        return stacks;
    }

    private static ItemStack stack(String itemId, int count) {
        ResourceLocation id = ResourceLocation.parse(itemId);
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, count);
    }
}
