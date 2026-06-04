package nl.streats1.ancientextensions.trade;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Optional;

/**
 * Cartographer sells the migration route chart (alternative to crafting with a journal).
 */
public final class CartographerChartTrades {

    /** Villager level index 2 (Journeyman), same tier as vanilla explorer maps. */
    public static final int CARTOGRAPHER_LEVEL = 2;
    public static final int EMERALD_COST = 7;
    public static final int MAX_USES = 12;
    public static final int XP_REWARD = 10;
    public static final float PRICE_MULTIPLIER = 0.05F;

    private CartographerChartTrades() {
    }

    public static VillagerTrades.ItemListing listing(Item chartItem) {
        return (merchant, random) -> createOffer(chartItem);
    }

    public static MerchantOffer createOffer(Item chartItem) {
        if (chartItem == null) {
            return null;
        }
        return new MerchantOffer(
                new ItemCost(Items.EMERALD, EMERALD_COST),
                Optional.of(new ItemCost(Items.COMPASS, 1)),
                new ItemStack(chartItem),
                MAX_USES,
                XP_REWARD,
                PRICE_MULTIPLIER
        );
    }
}
