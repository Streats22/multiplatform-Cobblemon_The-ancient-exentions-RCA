package com.streats.ancientextensions.fabric;

import com.streats.ancientextensions.command.AncientExtensionsCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class AncientExtensionsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(AncientExtensionsCommands::register);
    }
}
