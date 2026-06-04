package nl.streats1.ancientextensions.integration.cobblemon;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks Cobblemon ownership across party, PC, and Pokédex catch records.
 */
public final class CobblemonDexCompletion {

    public record Progress(int owned, int total, boolean complete) {
    }

    private CobblemonDexCompletion() {
    }

    public static Progress progress(ServerPlayer player) {
        List<Species> required = PokemonSpecies.getImplemented();
        if (required.isEmpty()) {
            return new Progress(0, 0, false);
        }
        Set<ResourceLocation> owned = collectOwnedSpecies(player);
        int count = 0;
        for (Species species : required) {
            if (owned.contains(species.getResourceIdentifier())) {
                count++;
            }
        }
        return new Progress(count, required.size(), count >= required.size());
    }

    public static boolean hasAllSpecies(ServerPlayer player) {
        return progress(player).complete();
    }

    private static Set<ResourceLocation> collectOwnedSpecies(ServerPlayer player) {
        Set<ResourceLocation> owned = new HashSet<>();
        var pokedex = Cobblemon.INSTANCE.getPlayerDataManager().getPokedexData(player);
        for (Species species : PokemonSpecies.getImplemented()) {
            ResourceLocation id = species.getResourceIdentifier();
            if (pokedex.getKnowledgeForSpecies(id) == PokedexEntryProgress.CAUGHT) {
                owned.add(id);
            }
        }

        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
        for (Pokemon pokemon : party) {
            if (pokemon != null) {
                owned.add(pokemon.getSpecies().getResourceIdentifier());
            }
        }

        PCStore pc = Cobblemon.INSTANCE.getStorage().getPC(player);
        for (Pokemon pokemon : pc) {
            if (pokemon != null) {
                owned.add(pokemon.getSpecies().getResourceIdentifier());
            }
        }
        return owned;
    }
}
