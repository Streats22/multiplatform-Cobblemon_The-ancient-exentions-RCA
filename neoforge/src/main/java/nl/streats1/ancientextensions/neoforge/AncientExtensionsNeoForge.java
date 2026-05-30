package nl.streats1.ancientextensions.neoforge;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.command.AncientExtensionsCommands;
import nl.streats1.ancientextensions.display.SurveyOriginEffects;
import nl.streats1.ancientextensions.menu.JournalMenuOpener;
import nl.streats1.ancientextensions.neoforge.client.AncientExtensionsNeoForgeClient;
import nl.streats1.ancientextensions.neoforge.data.ModAttachments;
import nl.streats1.ancientextensions.neoforge.data.NeoForgeSurveyBackend;
import nl.streats1.ancientextensions.neoforge.event.CobblemonEventHandlers;
import nl.streats1.ancientextensions.neoforge.registry.ModBlockEntities;
import nl.streats1.ancientextensions.neoforge.registry.ModBlocks;
import nl.streats1.ancientextensions.neoforge.registry.ModCreativeTabs;
import nl.streats1.ancientextensions.neoforge.registry.ModItems;
import nl.streats1.ancientextensions.neoforge.registry.ModMenus;
import nl.streats1.ancientextensions.neoforge.network.ModNetworking;
import nl.streats1.ancientextensions.neoforge.network.NeoForgeMenuOpenHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(AncientExtensionsConstants.MOD_ID)
public class AncientExtensionsNeoForge {

    public AncientExtensionsNeoForge(IEventBus modBus) {
        var context = new AncientExtensionsContext(new NeoForgeSurveyBackend());
        context.setOriginEffects(SurveyOriginEffects::apply);
        context.setPassportOpener(ModNetworking::openPassport);
        context.setJournalOpener(JournalMenuOpener::open);
        AncientExtensionsContext.init(context);

        NeoForgeMenuOpenHelper.register();
        ModAttachments.ATTACHMENTS.register(modBus);
        ModBlocks.register(modBus);
        ModBlockEntities.register(modBus);
        ModItems.register(modBus);
        ModMenus.register(modBus);
        modBus.addListener(ModNetworking::register);
        modBus.addListener(AncientExtensionsNeoForgeClient::registerScreens);
        modBus.addListener(event -> AncientExtensionsNeoForgeClient.initClientHooks());
        ModCreativeTabs.register(modBus);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        CobblemonEventHandlers.register(context);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        AncientExtensionsCommands.register(
                event.getDispatcher(),
                event.getBuildContext(),
                event.getCommandSelection()
        );
    }
}
