package nl.streats1.ancientextensions.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import nl.streats1.ancientextensions.AncientExtensionsConstants;

public record TabletActionPayload(byte action) implements CustomPacketPayload {

    public static final byte OPEN_JOURNAL = 0;
    public static final byte OPEN_PASSPORT = 1;
    public static final byte OPEN_CHART = 2;
    public static final byte CLAIM_REWARDS = 3;

    public static final Type<TabletActionPayload> TYPE = new Type<>(
            AncientExtensionsConstants.id("tablet_action")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TabletActionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE,
            TabletActionPayload::action,
            TabletActionPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
