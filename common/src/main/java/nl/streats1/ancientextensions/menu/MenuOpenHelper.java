package nl.streats1.ancientextensions.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public final class MenuOpenHelper {

    @FunctionalInterface
    public interface ExtraDataWriter {
        void write(RegistryFriendlyByteBuf buf);
    }

    @FunctionalInterface
    public interface Opener {
        void open(ServerPlayer player, MenuProvider provider, Object syncData, ExtraDataWriter writer);
    }

    private static Opener opener = (player, provider, syncData, writer) -> {
        throw new IllegalStateException("MenuOpenHelper has not been initialized");
    };

    private MenuOpenHelper() {
    }

    public static void setOpener(Opener opener) {
        MenuOpenHelper.opener = opener;
    }

    public static void open(ServerPlayer player, MenuProvider provider, Object syncData, ExtraDataWriter writer) {
        opener.open(player, provider, syncData, writer);
    }
}
