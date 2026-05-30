package nl.streats1.ancientextensions.menu;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.menu.sync.PassportOpenData;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.dex.ResearchTier;
import nl.streats1.ancientextensions.dex.SurveyOriginTown;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.registry.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class RegionalPassportMenu extends AbstractContainerMenu {

    public static final int WIDTH = 220;
    public static final int HEIGHT = 200;

    private final boolean stamped;
    private final String regionId;
    private final String townId;
    private final String holderName;
    private final int caughtSpecies;
    private final int researchPoints;
    private final ResearchTier tier;

    public RegionalPassportMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, PassportOpenData.STREAM_CODEC.decode(extraData));
    }

    public RegionalPassportMenu(int containerId, Inventory playerInventory, PassportOpenData data) {
        this(
                containerId,
                playerInventory,
                data.stamped(),
                data.regionId(),
                data.townId(),
                data.holderName(),
                data.caughtSpecies(),
                data.researchPoints(),
                data.tier()
        );
    }

    private RegionalPassportMenu(
            int containerId,
            Inventory playerInventory,
            boolean stamped,
            String regionId,
            String townId,
            String holderName,
            int caughtSpecies,
            int researchPoints,
            ResearchTier tier
    ) {
        super(ModMenuTypes.REGIONAL_PASSPORT, containerId);
        this.stamped = stamped;
        this.regionId = regionId;
        this.townId = townId;
        this.holderName = holderName;
        this.caughtSpecies = caughtSpecies;
        this.researchPoints = researchPoints;
        this.tier = tier;
    }

    public static RegionalPassportMenu forPlayer(int containerId, Inventory inventory, ServerPlayer player) {
        RegionalSurveyData data = AncientExtensionsContext.get().surveys().get(player);
        Optional<SurveyRegion> origin = data.getSurveyOrigin();
        return new RegionalPassportMenu(
                containerId,
                inventory,
                !data.showsPassportSetupScreen(),
                origin.map(SurveyRegion::getId).orElse(""),
                data.getSurveyOriginTown().map(SurveyOriginTown::getId).orElse(""),
                player.getGameProfile().getName(),
                data.getCaughtSpeciesCount(),
                data.getResearchPoints(),
                data.getTier()
        );
    }

    public static void writeExtraData(RegistryFriendlyByteBuf buf, RegionalSurveyData data, ServerPlayer player) {
        PassportOpenData.STREAM_CODEC.encode(buf, PassportOpenData.from(data, player));
    }

    public String getHolderName() {
        return holderName;
    }

    public boolean isStamped() {
        return stamped;
    }

    public Optional<SurveyRegion> getRegion() {
        return SurveyRegion.fromId(regionId);
    }

    public Optional<SurveyOriginTown> getTown() {
        return SurveyOriginTown.fromId(townId);
    }

    public int getCaughtSpecies() {
        return caughtSpecies;
    }

    public int getResearchPoints() {
        return researchPoints;
    }

    public ResearchTier getTier() {
        return tier;
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
