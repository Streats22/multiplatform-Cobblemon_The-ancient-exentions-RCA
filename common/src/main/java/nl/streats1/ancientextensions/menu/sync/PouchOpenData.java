package nl.streats1.ancientextensions.menu.sync;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;

public record PouchOpenData(boolean blockMenu, BlockPos blockPos, InteractionHand hand) {

    public static final StreamCodec<RegistryFriendlyByteBuf, PouchOpenData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> data.write(buf),
            PouchOpenData::read
    );

    public static PouchOpenData forBlock(BlockPos pos) {
        return new PouchOpenData(true, pos, InteractionHand.MAIN_HAND);
    }

    public static PouchOpenData forItem(InteractionHand hand) {
        return new PouchOpenData(false, BlockPos.ZERO, hand);
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(blockMenu);
        if (blockMenu) {
            buf.writeBlockPos(blockPos);
        } else {
            buf.writeEnum(hand);
        }
    }

    public static PouchOpenData read(RegistryFriendlyByteBuf buf) {
        boolean blockMenu = buf.readBoolean();
        if (blockMenu) {
            return forBlock(buf.readBlockPos());
        }
        return forItem(buf.readEnum(InteractionHand.class));
    }
}
