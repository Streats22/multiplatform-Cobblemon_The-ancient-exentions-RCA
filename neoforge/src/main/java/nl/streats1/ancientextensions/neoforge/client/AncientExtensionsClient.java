package nl.streats1.ancientextensions.neoforge.client;

import nl.streats1.ancientextensions.neoforge.registry.ModMenus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class AncientExtensionsClient {

    private AncientExtensionsClient() {
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.POKEBALL_POUCH.get(), PokeballPouchScreen::new);
        event.register(ModMenus.REGIONAL_PASSPORT.get(), RegionalPassportScreen::new);
    }
}
