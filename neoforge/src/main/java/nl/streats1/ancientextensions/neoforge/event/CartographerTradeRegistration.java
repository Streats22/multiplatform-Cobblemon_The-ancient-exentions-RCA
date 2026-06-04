package nl.streats1.ancientextensions.neoforge.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.registry.ModContent;
import nl.streats1.ancientextensions.trade.CartographerChartTrades;

import java.util.List;

@EventBusSubscriber(modid = AncientExtensionsConstants.MOD_ID)
public final class CartographerTradeRegistration {

    private CartographerTradeRegistration() {
    }

    @SubscribeEvent
    public static void addTrades(VillagerTradesEvent event) {
        if (event.getType() != VillagerProfession.CARTOGRAPHER) {
            return;
        }
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        List<VillagerTrades.ItemListing> journeyman = trades.get(CartographerChartTrades.CARTOGRAPHER_LEVEL);
        if (journeyman != null) {
            journeyman.add(CartographerChartTrades.listing(ModContent.MIGRATION_ROUTE_CHART));
        }
    }
}
