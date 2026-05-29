package com.streats.ancientextensions.neoforge.network;

import com.streats.ancientextensions.AncientExtensionsConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SelectSurveyRegionPayload(String regionId) implements CustomPacketPayload {

    public static final Type<SelectSurveyRegionPayload> TYPE =
            new Type<>(AncientExtensionsConstants.id("select_survey_region"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectSurveyRegionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    SelectSurveyRegionPayload::regionId,
                    SelectSurveyRegionPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
