package nl.streats1.ancientextensions.fabric.event;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.npc.VillagerProfession;
import nl.streats1.ancientextensions.registry.ModContent;
import nl.streats1.ancientextensions.trade.CartographerChartTrades;

public final class CartographerTradeRegistration {

    private CartographerTradeRegistration() {
    }

    public static void register() {
        TradeOfferHelper.registerVillagerOffers(
                VillagerProfession.CARTOGRAPHER,
                CartographerChartTrades.CARTOGRAPHER_LEVEL,
                factories -> factories.add(CartographerChartTrades.listing(ModContent.MIGRATION_ROUTE_CHART))
        );
    }
}
