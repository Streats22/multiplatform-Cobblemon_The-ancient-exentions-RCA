package nl.streats1.ancientextensions.neoforge.network;

import nl.streats1.ancientextensions.menu.MenuOpenHelper;

public final class NeoForgeMenuOpenHelper {

    private NeoForgeMenuOpenHelper() {
    }

    public static void register() {
        MenuOpenHelper.setOpener((player, provider, syncData, writer) ->
                player.openMenu(provider, writer::write)
        );
    }
}
