package nl.streats1.ancientextensions.menu.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

import nl.streats1.ancientextensions.config.ShinyCharmConfig;
import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.dex.ResearchTier;
import nl.streats1.ancientextensions.dex.ShinyCharmService;
import nl.streats1.ancientextensions.dex.SurveyOriginTown;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import nl.streats1.ancientextensions.integration.cobblemon.CobblemonDexCompletion;

public record PassportOpenData(
        boolean stamped,
        String regionId,
        String townId,
        String holderName,
        int caughtSpecies,
        int researchPoints,
        String tierName,
        boolean shinyCharmEnabled,
        boolean shinyCharmClaimed,
        boolean shinyCharmCanClaim,
        boolean shinyCharmActive,
        int cobblemonOwned,
        int cobblemonTotal
) {

    public static final StreamCodec<RegistryFriendlyByteBuf, PassportOpenData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {
                ByteBufCodecs.BOOL.encode(buf, data.stamped());
                ByteBufCodecs.STRING_UTF8.encode(buf, data.regionId());
                ByteBufCodecs.STRING_UTF8.encode(buf, data.townId());
                ByteBufCodecs.STRING_UTF8.encode(buf, data.holderName());
                ByteBufCodecs.VAR_INT.encode(buf, data.caughtSpecies());
                ByteBufCodecs.VAR_INT.encode(buf, data.researchPoints());
                ByteBufCodecs.STRING_UTF8.encode(buf, data.tierName());
                ByteBufCodecs.BOOL.encode(buf, data.shinyCharmEnabled());
                ByteBufCodecs.BOOL.encode(buf, data.shinyCharmClaimed());
                ByteBufCodecs.BOOL.encode(buf, data.shinyCharmCanClaim());
                ByteBufCodecs.BOOL.encode(buf, data.shinyCharmActive());
                ByteBufCodecs.VAR_INT.encode(buf, data.cobblemonOwned());
                ByteBufCodecs.VAR_INT.encode(buf, data.cobblemonTotal());
            },
            buf -> new PassportOpenData(
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf)
            )
    );

    public static PassportOpenData from(RegionalSurveyData data, ServerPlayer player) {
        Optional<SurveyRegion> origin = data.getSurveyOrigin();
        CobblemonDexCompletion.Progress dex = ShinyCharmService.dexProgress(player);
        return new PassportOpenData(
                !data.showsPassportSetupScreen(),
                origin.map(SurveyRegion::getId).orElse(""),
                data.getSurveyOriginTown().map(SurveyOriginTown::getId).orElse(""),
                player.getGameProfile().getName(),
                data.getCaughtSpeciesCount(),
                data.getResearchPoints(),
                data.getTier().name(),
                ShinyCharmConfig.enabled(),
                data.hasClaimedShinyCharm(),
                ShinyCharmService.canClaim(player),
                ShinyCharmService.hasActiveBonus(player),
                dex.owned(),
                dex.total()
        );
    }

    public ResearchTier tier() {
        return ResearchTier.valueOf(tierName);
    }
}
