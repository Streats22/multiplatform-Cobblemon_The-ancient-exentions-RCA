package nl.streats1.ancientextensions.fabric.event;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import nl.streats1.ancientextensions.AncientExtensionsContext;

public final class CobblemonEventHandlers {

    private CobblemonEventHandlers() {
    }

    public static void register(AncientExtensionsContext context) {
        CobblemonEvents.POKEMON_CAPTURED.subscribe((PokemonCapturedEvent event) -> {
            ServerPlayer player = event.getPlayer();
            ResourceLocation speciesId = event.getPokemon().getSpecies().getResourceIdentifier();
            context.surveys().onSpeciesCaptured(player, speciesId);
        });
    }
}
