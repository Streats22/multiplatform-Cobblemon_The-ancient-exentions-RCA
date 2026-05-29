package com.streats.ancientextensions.neoforge.client;

import com.streats.ancientextensions.neoforge.registry.ModMenus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class AncientExtensionsClient {

    private AncientExtensionsClient() {
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.POKEBALL_POUCH.get(), PokeballPouchScreen::new);
    }
}
