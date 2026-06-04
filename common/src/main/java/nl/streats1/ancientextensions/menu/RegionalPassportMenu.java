package nl.streats1.ancientextensions.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.dex.ResearchTier;
import nl.streats1.ancientextensions.dex.SurveyOriginTown;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.menu.sync.PassportOpenData;
import nl.streats1.ancientextensions.registry.ModMenuTypes;

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
    private final boolean shinyCharmEnabled;
    private final boolean shinyCharmClaimed;
    private final boolean shinyCharmCanClaim;
    private final boolean shinyCharmActive;
    private final int cobblemonOwned;
    private final int cobblemonTotal;

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
                data.tier(),
                data.shinyCharmEnabled(),
                data.shinyCharmClaimed(),
                data.shinyCharmCanClaim(),
                data.shinyCharmActive(),
                data.cobblemonOwned(),
                data.cobblemonTotal()
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
            ResearchTier tier,
            boolean shinyCharmEnabled,
            boolean shinyCharmClaimed,
            boolean shinyCharmCanClaim,
            boolean shinyCharmActive,
            int cobblemonOwned,
            int cobblemonTotal
    ) {
        super(ModMenuTypes.REGIONAL_PASSPORT, containerId);
        this.stamped = stamped;
        this.regionId = regionId;
        this.townId = townId;
        this.holderName = holderName;
        this.caughtSpecies = caughtSpecies;
        this.researchPoints = researchPoints;
        this.tier = tier;
        this.shinyCharmEnabled = shinyCharmEnabled;
        this.shinyCharmClaimed = shinyCharmClaimed;
        this.shinyCharmCanClaim = shinyCharmCanClaim;
        this.shinyCharmActive = shinyCharmActive;
        this.cobblemonOwned = cobblemonOwned;
        this.cobblemonTotal = cobblemonTotal;
    }

    public static RegionalPassportMenu forPlayer(int containerId, Inventory inventory, ServerPlayer player) {
        PassportOpenData data = PassportOpenData.from(AncientExtensionsContext.get().surveys().get(player), player);
        return new RegionalPassportMenu(containerId, inventory, data);
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

    public boolean isShinyCharmEnabled() {
        return shinyCharmEnabled;
    }

    public boolean isShinyCharmClaimed() {
        return shinyCharmClaimed;
    }

    public boolean canClaimShinyCharm() {
        return shinyCharmCanClaim;
    }

    public boolean isShinyCharmActive() {
        return shinyCharmActive;
    }

    public int getCobblemonOwned() {
        return cobblemonOwned;
    }

    public int getCobblemonTotal() {
        return cobblemonTotal;
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
