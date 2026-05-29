package com.streats.ancientextensions.neoforge;

import com.streats.ancientextensions.AncientExtensionsConstants;
import com.streats.ancientextensions.command.AncientExtensionsCommands;
import com.streats.ancientextensions.dex.PlayerSurveyStorage;
import com.streats.ancientextensions.neoforge.data.ModAttachments;
import com.streats.ancientextensions.neoforge.data.NeoForgeSurveyBackend;
import com.streats.ancientextensions.neoforge.client.AncientExtensionsClient;
import com.streats.ancientextensions.neoforge.event.CobblemonEventHandlers;
import com.streats.ancientextensions.neoforge.registry.ModBlockEntities;
import com.streats.ancientextensions.neoforge.registry.ModBlocks;
import com.streats.ancientextensions.neoforge.registry.ModCreativeTabs;
import com.streats.ancientextensions.neoforge.registry.ModItems;
import com.streats.ancientextensions.neoforge.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(AncientExtensionsConstants.MOD_ID)
public class AncientExtensionsNeoForge {

    public AncientExtensionsNeoForge(IEventBus modBus) {
        ModAttachments.ATTACHMENTS.register(modBus);
        ModBlocks.register(modBus);
        ModBlockEntities.register(modBus);
        ModItems.register(modBus);
        ModMenus.register(modBus);
        modBus.addListener(AncientExtensionsClient::registerScreens);
        ModCreativeTabs.register(modBus);
        PlayerSurveyStorage.setBackend(new NeoForgeSurveyBackend());
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        CobblemonEventHandlers.register();
    }

    private void registerCommands(RegisterCommandsEvent event) {
        AncientExtensionsCommands.register(
                event.getDispatcher(),
                event.getBuildContext(),
                event.getCommandSelection()
        );
    }
}
