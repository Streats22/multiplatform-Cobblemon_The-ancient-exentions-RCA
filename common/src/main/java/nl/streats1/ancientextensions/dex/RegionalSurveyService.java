package nl.streats1.ancientextensions.dex;

import nl.streats1.ancientextensions.config.AncientExtensionsDesign;
import nl.streats1.ancientextensions.migration.MigrationService;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Regional Survey — catch-only dex (see {@link AncientExtensionsDesign#DEX_CATCH_ONLY}).
 */
public final class RegionalSurveyService {

    public static final int POINTS_PER_NEW_SPECIES = 5;
    public static final int POINTS_FIRST_CATCH_BONUS = 10;

    private RegionalSurveyService() {
    }

    public static RegionalSurveyData get(ServerPlayer player) {
        return PlayerSurveyStorage.get(player);
    }

    public static void save(ServerPlayer player, RegionalSurveyData data) {
        PlayerSurveyStorage.save(player, data);
    }

    /**
     * Called from {@link com.cobblemon.mod.common.api.events.CobblemonEvents#POKEMON_CAPTURED} only.
     */
    public static boolean onSpeciesCaptured(ServerPlayer player, ResourceLocation speciesId) {
        RegionalSurveyData data = get(player);
        int countBefore = data.getCaughtSpeciesCount();
        boolean isNew = data.registerCaughtSpecies(speciesId, POINTS_PER_NEW_SPECIES);
        if (isNew && countBefore == 0) {
            data.addResearchPoints(POINTS_FIRST_CATCH_BONUS);
        }

        ResearchTier before = data.getTier();
        MigrationService.onSpeciesCaptured(player, speciesId);
        save(player, data);

        ResearchTier after = data.getTier();
        if (after != before) {
            player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.dex.tier_up",
                    after.displayName()
            ));
        }

        if (isNew) {
            player.sendSystemMessage(Component.translatable(
                    "ancient_extensions.dex.caught",
                    speciesId.toString(),
                    data.getResearchPoints()
            ));
        }
        return isNew;
    }
}
