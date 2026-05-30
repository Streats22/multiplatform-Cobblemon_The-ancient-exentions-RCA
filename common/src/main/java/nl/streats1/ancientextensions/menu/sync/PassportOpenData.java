package nl.streats1.ancientextensions.menu.sync;

import nl.streats1.ancientextensions.dex.RegionalSurveyData;
import nl.streats1.ancientextensions.dex.ResearchTier;
import nl.streats1.ancientextensions.dex.SurveyRegion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public record PassportOpenData(
        boolean stamped,
        String regionId,
        String holderName,
        int caughtSpecies,
        int researchPoints,
        String tierName
) {

    public static final StreamCodec<RegistryFriendlyByteBuf, PassportOpenData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PassportOpenData::stamped,
            ByteBufCodecs.STRING_UTF8, PassportOpenData::regionId,
            ByteBufCodecs.STRING_UTF8, PassportOpenData::holderName,
            ByteBufCodecs.VAR_INT, PassportOpenData::caughtSpecies,
            ByteBufCodecs.VAR_INT, PassportOpenData::researchPoints,
            ByteBufCodecs.STRING_UTF8, PassportOpenData::tierName,
            PassportOpenData::new
    );

    public static PassportOpenData from(RegionalSurveyData data, ServerPlayer player) {
        Optional<SurveyRegion> origin = data.getSurveyOrigin();
        return new PassportOpenData(
                origin.isPresent(),
                origin.map(SurveyRegion::getId).orElse(""),
                player.getGameProfile().getName(),
                data.getCaughtSpeciesCount(),
                data.getResearchPoints(),
                data.getTier().name()
        );
    }

    public ResearchTier tier() {
        return ResearchTier.valueOf(tierName);
    }
}
