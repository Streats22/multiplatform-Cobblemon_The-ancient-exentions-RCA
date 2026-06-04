package nl.streats1.ancientextensions.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import nl.streats1.ancientextensions.AncientExtensionsConstants;

/**
 * Server → client: place a migration route waypoint in Xaero's Minimap / World Map.
 */
public record MigrationWaypointPayload(
        ResourceLocation dimension,
        int x,
        int y,
        int z,
        String label
) implements CustomPacketPayload {

    public static final Type<MigrationWaypointPayload> TYPE =
            new Type<>(AncientExtensionsConstants.id("migration_waypoint"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MigrationWaypointPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC,
                    MigrationWaypointPayload::dimension,
                    ByteBufCodecs.VAR_INT,
                    MigrationWaypointPayload::x,
                    ByteBufCodecs.VAR_INT,
                    MigrationWaypointPayload::y,
                    ByteBufCodecs.VAR_INT,
                    MigrationWaypointPayload::z,
                    ByteBufCodecs.STRING_UTF8,
                    MigrationWaypointPayload::label,
                    MigrationWaypointPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
