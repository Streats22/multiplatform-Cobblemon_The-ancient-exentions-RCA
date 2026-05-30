package nl.streats1.ancientextensions.fabric;

import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.command.AncientExtensionsCommands;
import nl.streats1.ancientextensions.display.SurveyOriginEffects;
import nl.streats1.ancientextensions.fabric.data.FabricSurveyBackend;
import nl.streats1.ancientextensions.fabric.event.CobblemonEventHandlers;
import nl.streats1.ancientextensions.fabric.event.PlayerJoinHandlers;
import nl.streats1.ancientextensions.fabric.network.FabricMenuOpenHelper;
import nl.streats1.ancientextensions.fabric.network.FabricNetworking;
import nl.streats1.ancientextensions.fabric.registry.ModRegistries;
import nl.streats1.ancientextensions.menu.JournalMenuOpener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class AncientExtensionsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        var context = new AncientExtensionsContext(new FabricSurveyBackend());
        context.setOriginEffects(SurveyOriginEffects::apply);
        context.setPassportOpener(FabricNetworking::openPassport);
        context.setJournalOpener(JournalMenuOpener::open);
        AncientExtensionsContext.init(context);

        ModRegistries.register();
        FabricMenuOpenHelper.register();
        FabricNetworking.register();
        PlayerJoinHandlers.register();

        CommandRegistrationCallback.EVENT.register(AncientExtensionsCommands::register);
        CobblemonEventHandlers.register(context);
    }
}
