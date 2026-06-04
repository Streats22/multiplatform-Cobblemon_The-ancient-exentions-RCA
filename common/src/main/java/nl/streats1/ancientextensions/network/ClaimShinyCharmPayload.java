package nl.streats1.ancientextensions.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import nl.streats1.ancientextensions.AncientExtensionsConstants;

public record ClaimShinyCharmPayload() implements CustomPacketPayload {

    public static final Type<ClaimShinyCharmPayload> TYPE =
            new Type<>(AncientExtensionsConstants.id("claim_shiny_charm"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClaimShinyCharmPayload> STREAM_CODEC =
            StreamCodec.unit(new ClaimShinyCharmPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
