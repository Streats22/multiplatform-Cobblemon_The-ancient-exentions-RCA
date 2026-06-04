package nl.streats1.ancientextensions.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.dex.FieldSurveyTabletReport;
import nl.streats1.ancientextensions.menu.sync.TabletOpenData;
import nl.streats1.ancientextensions.registry.ModMenuTypes;

public class FieldSurveyTabletMenu extends AbstractContainerMenu {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 256;

    private final List<net.minecraft.network.chat.Component> lines;
    private final int unclaimedRewardCount;

    public FieldSurveyTabletMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, TabletOpenData.STREAM_CODEC.decode(extraData));
    }

    public FieldSurveyTabletMenu(int containerId, Inventory playerInventory, TabletOpenData data) {
        this(containerId, data.lines(), data.unclaimedRewardCount());
    }

    private FieldSurveyTabletMenu(int containerId, List<net.minecraft.network.chat.Component> lines, int unclaimedRewardCount) {
        super(ModMenuTypes.FIELD_SURVEY_TABLET, containerId);
        this.lines = lines;
        this.unclaimedRewardCount = unclaimedRewardCount;
    }

    public static FieldSurveyTabletMenu forPlayer(int containerId, Inventory inventory, ServerPlayer player) {
        TabletOpenData data = buildOpenData(player);
        return new FieldSurveyTabletMenu(containerId, data.lines(), data.unclaimedRewardCount());
    }

    public static TabletOpenData buildOpenData(ServerPlayer player) {
        int unclaimed = AncientExtensionsContext.get().tierRewards().unclaimedCount(
                AncientExtensionsContext.get().surveys().get(player)
        );
        return new TabletOpenData(FieldSurveyTabletReport.buildLines(player), unclaimed);
    }

    public List<net.minecraft.network.chat.Component> getLines() {
        return lines;
    }

    public int getUnclaimedRewardCount() {
        return unclaimedRewardCount;
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
