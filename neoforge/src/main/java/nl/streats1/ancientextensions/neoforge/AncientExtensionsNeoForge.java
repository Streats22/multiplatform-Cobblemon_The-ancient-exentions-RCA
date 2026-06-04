package nl.streats1.ancientextensions.neoforge;

import nl.streats1.ancientextensions.AncientExtensionsConstants;
import nl.streats1.ancientextensions.AncientExtensionsContext;
import nl.streats1.ancientextensions.command.AncientExtensionsCommands;
import nl.streats1.ancientextensions.display.SurveyOriginEffects;
import nl.streats1.ancientextensions.menu.JournalMenuOpener;
import nl.streats1.ancientextensions.neoforge.client.AncientExtensionsNeoForgeClient;
import nl.streats1.ancientextensions.neoforge.config.NeoForgeCampConfig;
import nl.streats1.ancientextensions.neoforge.data.ModAttachments;
import nl.streats1.ancientextensions.neoforge.data.NeoForgeSurveyBackend;
import nl.streats1.ancientextensions.neoforge.event.CartographerTradeRegistration;
import nl.streats1.ancientextensions.neoforge.event.CobblemonEventHandlers;
import nl.streats1.ancientextensions.neoforge.integration.create.CreateCompat;
import nl.streats1.ancientextensions.neoforge.integration.sophisticated.SophisticatedBackpacksCompat;
import nl.streats1.ancientextensions.neoforge.registry.ModBlockEntities;
import nl.streats1.ancientextensions.neoforge.registry.ModBlocks;
import nl.streats1.ancientextensions.neoforge.registry.ModCreativeTabs;
import nl.streats1.ancientextensions.neoforge.registry.ModItems;
import nl.streats1.ancientextensions.neoforge.registry.ModMenus;
import nl.streats1.ancientextensions.registry.ModRecipeSerializers;
import nl.streats1.ancientextensions.neoforge.registry.NeoForgeRecipeSerializers;
import nl.streats1.ancientextensions.neoforge.network.ModNetworking;
import nl.streats1.ancientextensions.neoforge.network.NeoForgeMenuOpenHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(AncientExtensionsConstants.MOD_ID)
public class AncientExtensionsNeoForge {

    public AncientExtensionsNeoForge(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, NeoForgeCampConfig.SPEC);
        modBus.addListener(this::onConfigLoad);
        modBus.addListener(this::onConfigReload);

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
        NeoForgeRecipeSerializers.register(modBus);
        modBus.addListener((net.neoforged.neoforge.registries.RegisterEvent event) -> {
            if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER)) {
                ModRecipeSerializers.POKEBALL_POUCH = NeoForgeRecipeSerializers.POKEBALL_POUCH.get();
            }
        });
        ModMenus.register(modBus);
        modBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> {
            CreateCompat.init();
            SophisticatedBackpacksCompat.init();
        }));
        modBus.addListener(ModNetworking::register);
        modBus.addListener(AncientExtensionsNeoForgeClient::registerScreens);
        modBus.addListener(AncientExtensionsNeoForgeClient::registerRenderers);
        modBus.addListener((FMLClientSetupEvent event) ->
                event.enqueueWork(AncientExtensionsNeoForgeClient::initClientHooks));
        ModCreativeTabs.register(modBus);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.register(CartographerTradeRegistration.class);
        CobblemonEventHandlers.register(context);
    }

    private void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == NeoForgeCampConfig.SPEC) {
            NeoForgeCampConfig.sync();
        }
    }

    private void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == NeoForgeCampConfig.SPEC) {
            NeoForgeCampConfig.sync();
        }
    }

    private void registerCommands(RegisterCommandsEvent event) {
        AncientExtensionsCommands.register(
                event.getDispatcher(),
                event.getBuildContext(),
                event.getCommandSelection()
        );
    }
}
