package nl.streats1.ancientextensions.network;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Empty tier id claims every pending rank reward. */
public record ClaimTierRewardPayload(String tierId) implements CustomPacketPayload {

    public static final Type<ClaimTierRewardPayload> TYPE =
            new Type<>(AncientExtensionsConstants.id("claim_tier_reward"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClaimTierRewardPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    ClaimTierRewardPayload::tierId,
                    ClaimTierRewardPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
