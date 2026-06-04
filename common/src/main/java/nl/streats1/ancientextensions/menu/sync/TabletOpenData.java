package nl.streats1.ancientextensions.menu.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record TabletOpenData(List<Component> lines, int unclaimedRewardCount) {

    public static final StreamCodec<RegistryFriendlyByteBuf, TabletOpenData> STREAM_CODEC = StreamCodec.of(
            TabletOpenData::write,
            TabletOpenData::read
    );

    public static void write(RegistryFriendlyByteBuf buf, TabletOpenData data) {
        buf.writeVarInt(data.lines().size());
        for (Component line : data.lines()) {
            ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, line);
        }
        ByteBufCodecs.VAR_INT.encode(buf, data.unclaimedRewardCount());
    }

    public static TabletOpenData read(RegistryFriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<Component> lines;
        if (count <= 0) {
            lines = List.of();
        } else {
            lines = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                lines.add(ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buf));
            }
            lines = Collections.unmodifiableList(lines);
        }
        int unclaimedRewardCount = ByteBufCodecs.VAR_INT.decode(buf);
        return new TabletOpenData(lines, unclaimedRewardCount);
    }
}
