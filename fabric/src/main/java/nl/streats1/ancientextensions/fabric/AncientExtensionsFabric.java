package nl.streats1.ancientextensions.fabric;

import nl.streats1.ancientextensions.command.AncientExtensionsCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class AncientExtensionsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(AncientExtensionsCommands::register);
    }
}
