package nl.streats1.ancientextensions.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import nl.streats1.ancientextensions.field.FieldSurveyCalendarReport;
import nl.streats1.ancientextensions.menu.sync.ChartOpenData;
import nl.streats1.ancientextensions.registry.ModMenuTypes;

public class FieldSurveyCalendarMenu extends AbstractContainerMenu {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 256;

    private final List<Component> lines;

    public FieldSurveyCalendarMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, ChartOpenData.STREAM_CODEC.decode(extraData));
    }

    public FieldSurveyCalendarMenu(int containerId, Inventory playerInventory, ChartOpenData data) {
        this(containerId, data.lines());
    }

    private FieldSurveyCalendarMenu(int containerId, List<Component> lines) {
        super(ModMenuTypes.FIELD_SURVEY_CALENDAR, containerId);
        this.lines = lines;
    }

    public static FieldSurveyCalendarMenu forBlock(int containerId, Inventory inventory, ServerPlayer player, BlockPos pos) {
        return new FieldSurveyCalendarMenu(containerId, inventory, buildOpenData(player.serverLevel(), pos));
    }

    public static ChartOpenData buildOpenData(ServerLevel level, BlockPos pos) {
        return new ChartOpenData(FieldSurveyCalendarReport.buildLines(level, pos));
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
