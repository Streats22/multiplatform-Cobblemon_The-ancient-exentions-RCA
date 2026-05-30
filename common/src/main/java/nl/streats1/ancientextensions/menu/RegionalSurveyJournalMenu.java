package nl.streats1.ancientextensions.menu;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.dex.SurveyJournalReport;
import nl.streats1.ancientextensions.migration.MigrationSeason;
import nl.streats1.ancientextensions.migration.MigrationSeasonClock;
import nl.streats1.ancientextensions.menu.sync.JournalOpenData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import nl.streats1.ancientextensions.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class RegionalSurveyJournalMenu extends AbstractContainerMenu {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 256;

    private final List<Component> lines;
    private final int unclaimedRewardCount;

    public RegionalSurveyJournalMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, JournalOpenData.STREAM_CODEC.decode(extraData));
    }

    public RegionalSurveyJournalMenu(int containerId, Inventory playerInventory, JournalOpenData data) {
        this(containerId, data.lines(), data.unclaimedRewardCount());
    }

    private RegionalSurveyJournalMenu(int containerId, List<Component> lines, int unclaimedRewardCount) {
        super(ModMenuTypes.REGIONAL_SURVEY_JOURNAL, containerId);
        this.lines = lines;
        this.unclaimedRewardCount = unclaimedRewardCount;
    }

    public static RegionalSurveyJournalMenu forPlayer(int containerId, Inventory inventory, ServerPlayer player) {
        JournalOpenData data = buildOpenData(player);
        return new RegionalSurveyJournalMenu(containerId, data.lines(), data.unclaimedRewardCount());
    }

    public static void writeExtraData(RegistryFriendlyByteBuf buf, ServerPlayer player) {
        JournalOpenData.STREAM_CODEC.encode(buf, buildOpenData(player));
    }

    public static JournalOpenData buildOpenData(ServerPlayer player) {
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        MigrationSeason season = MigrationSeasonClock.currentSeason(player.serverLevel());
        data.syncMigrationSeason(season);
        int unclaimed = AncientExtensionsContext.get().tierRewards().unclaimedCount(data);
        return new JournalOpenData(SurveyJournalReport.buildLines(data, season), unclaimed);
    }

    private static List<Component> loadLines(ServerPlayer player) {
        return buildOpenData(player).lines();
    }

    public static List<Component> loadLinesForPlayer(ServerPlayer player) {
        return loadLines(player);
    }

    public List<Component> getLines() {
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
