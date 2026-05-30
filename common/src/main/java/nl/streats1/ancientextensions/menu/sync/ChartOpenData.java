package nl.streats1.ancientextensions.menu.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ChartOpenData(List<Component> lines) {

    public static final StreamCodec<RegistryFriendlyByteBuf, ChartOpenData> STREAM_CODEC = StreamCodec.of(
            ChartOpenData::write,
            ChartOpenData::read
    );

    public static void write(RegistryFriendlyByteBuf buf, ChartOpenData data) {
        buf.writeVarInt(data.lines().size());
        for (Component line : data.lines()) {
            ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, line);
        }
    }

    public static ChartOpenData read(RegistryFriendlyByteBuf buf) {
        int count = buf.readVarInt();
        if (count <= 0) {
            return new ChartOpenData(List.of());
        }
        List<Component> lines = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            lines.add(ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buf));
        }
        return new ChartOpenData(Collections.unmodifiableList(lines));
    }
}
