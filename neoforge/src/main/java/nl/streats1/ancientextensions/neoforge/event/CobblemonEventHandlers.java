package nl.streats1.ancientextensions.neoforge.event;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import nl.streats1.ancientextensions.integration.cobblemon.ShinyCharmCobblemonHooks;
import nl.streats1.ancientextensions.AncientExtensionsContext;

public final class CobblemonEventHandlers {

    private CobblemonEventHandlers() {
    }

    public static void register(AncientExtensionsContext context) {
        ShinyCharmCobblemonHooks.register();
        // Catch-only Regional Survey — PC registration does not count.
        CobblemonEvents.POKEMON_CAPTURED.subscribe((PokemonCapturedEvent event) -> {
            ServerPlayer player = event.getPlayer();
            ResourceLocation speciesId = event.getPokemon().getSpecies().getResourceIdentifier();
            context.surveys().onSpeciesCaptured(player, speciesId);
        });
    }
}
