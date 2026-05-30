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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegionalSurveyJournalMenu extends AbstractContainerMenu {

    public static final int WIDTH = 220;
    public static final int HEIGHT = 220;

    private final List<Component> lines;

    public RegionalSurveyJournalMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, JournalOpenData.STREAM_CODEC.decode(extraData));
    }

    public RegionalSurveyJournalMenu(int containerId, Inventory playerInventory, JournalOpenData data) {
        this(containerId, data.lines());
    }

    private RegionalSurveyJournalMenu(int containerId, List<Component> lines) {
        super(ModMenuTypes.REGIONAL_SURVEY_JOURNAL, containerId);
        this.lines = lines;
    }

    public static RegionalSurveyJournalMenu forPlayer(int containerId, Inventory inventory, ServerPlayer player) {
        return new RegionalSurveyJournalMenu(containerId, loadLines(player));
    }

    public static void writeExtraData(RegistryFriendlyByteBuf buf, ServerPlayer player) {
        JournalOpenData.STREAM_CODEC.encode(buf, new JournalOpenData(loadLines(player)));
    }

    public static List<Component> loadLinesForPlayer(ServerPlayer player) {
        return loadLines(player);
    }

    private static List<Component> loadLines(ServerPlayer player) {
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        MigrationSeason season = MigrationSeasonClock.currentSeason(player.serverLevel());
        data.syncMigrationSeason(season);
        return SurveyJournalReport.buildLines(data, season);
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
