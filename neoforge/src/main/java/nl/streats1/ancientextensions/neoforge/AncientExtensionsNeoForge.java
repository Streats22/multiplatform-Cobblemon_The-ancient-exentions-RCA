package nl.streats1.ancientextensions.neoforge;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.command.AncientExtensionsCommands;
import nl.streats1.ancientextensions.dex.PlayerSurveyStorage;
import nl.streats1.ancientextensions.dex.SurveyOriginHooks;
import nl.streats1.ancientextensions.neoforge.display.SurveyOriginEffects;
import nl.streats1.ancientextensions.neoforge.data.ModAttachments;
import nl.streats1.ancientextensions.neoforge.data.NeoForgeSurveyBackend;
import nl.streats1.ancientextensions.neoforge.client.AncientExtensionsClient;
import nl.streats1.ancientextensions.neoforge.event.CobblemonEventHandlers;
import nl.streats1.ancientextensions.neoforge.registry.ModBlockEntities;
import nl.streats1.ancientextensions.neoforge.registry.ModBlocks;
import nl.streats1.ancientextensions.neoforge.registry.ModCreativeTabs;
import nl.streats1.ancientextensions.neoforge.registry.ModItems;
import nl.streats1.ancientextensions.neoforge.network.ModNetworking;
import nl.streats1.ancientextensions.neoforge.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(AncientExtensionsConstants.MOD_ID)
public class AncientExtensionsNeoForge {

    public AncientExtensionsNeoForge(IEventBus modBus) {
        SurveyOriginHooks.setHandler(SurveyOriginEffects::apply);
        ModAttachments.ATTACHMENTS.register(modBus);
        ModBlocks.register(modBus);
        ModBlockEntities.register(modBus);
        ModItems.register(modBus);
        ModMenus.register(modBus);
        modBus.addListener(ModNetworking::register);
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
