package nl.streats1.ancientextensions.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import nl.streats1.ancientextensions.AncientExtensionsConstants;

public record SelectSurveyRegionPayload(String regionId, String townId) implements CustomPacketPayload {

    public static final Type<SelectSurveyRegionPayload> TYPE =
            new Type<>(AncientExtensionsConstants.id("select_survey_region"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectSurveyRegionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    SelectSurveyRegionPayload::regionId,
                    ByteBufCodecs.STRING_UTF8,
                    SelectSurveyRegionPayload::townId,
                    SelectSurveyRegionPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
