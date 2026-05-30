package nl.streats1.ancientextensions.neoforge.event;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import nl.streats1.ancientextensions.dex.RegionalSurveyService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class CobblemonEventHandlers {

    private CobblemonEventHandlers() {
    }

    public static void register() {
        // Catch-only Regional Survey — PC registration does not count.
        CobblemonEvents.POKEMON_CAPTURED.subscribe((PokemonCapturedEvent event) -> {
            ServerPlayer player = event.getPlayer();
            ResourceLocation speciesId = event.getPokemon().getSpecies().getResourceIdentifier();
            RegionalSurveyService.onSpeciesCaptured(player, speciesId);
        });
    }
}
