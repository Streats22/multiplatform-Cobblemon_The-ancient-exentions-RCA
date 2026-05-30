package nl.streats1.ancientextensions.menu;

import nl.streats1.ancientextensions.migration.MigrationRouteChartReport;
import nl.streats1.ancientextensions.menu.sync.ChartOpenData;
import nl.streats1.ancientextensions.registry.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MigrationRouteChartMenu extends AbstractContainerMenu {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 256;

    private final List<Component> lines;

    public MigrationRouteChartMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, ChartOpenData.STREAM_CODEC.decode(extraData));
    }

    public MigrationRouteChartMenu(int containerId, Inventory playerInventory, ChartOpenData data) {
        this(containerId, data.lines());
    }

    private MigrationRouteChartMenu(int containerId, List<Component> lines) {
        super(ModMenuTypes.MIGRATION_ROUTE_CHART, containerId);
        this.lines = lines;
    }

    public static MigrationRouteChartMenu forPlayer(int containerId, Inventory inventory, ServerPlayer player) {
        return new MigrationRouteChartMenu(containerId, inventory, buildOpenData(player));
    }

    public static ChartOpenData buildOpenData(ServerPlayer player) {
        return new ChartOpenData(MigrationRouteChartReport.buildLines(player));
    }

    public List<Component> getLines() {
        return lines;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
