package nl.streats1.ancientextensions.menu.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record JournalOpenData(List<Component> lines) {

    public static final StreamCodec<RegistryFriendlyByteBuf, JournalOpenData> STREAM_CODEC = StreamCodec.of(
            JournalOpenData::write,
            JournalOpenData::read
    );

    public static void write(RegistryFriendlyByteBuf buf, JournalOpenData data) {
        buf.writeVarInt(data.lines().size());
        for (Component line : data.lines()) {
            ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, line);
        }
    }

    public static JournalOpenData read(RegistryFriendlyByteBuf buf) {
        int count = buf.readVarInt();
        if (count <= 0) {
            return new JournalOpenData(List.of());
        }
        List<Component> lines = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            lines.add(ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buf));
        }
        return new JournalOpenData(Collections.unmodifiableList(lines));
    }
}
