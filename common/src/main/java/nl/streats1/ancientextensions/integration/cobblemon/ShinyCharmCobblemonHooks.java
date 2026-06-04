package nl.streats1.ancientextensions.integration.cobblemon;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.ShinyChanceCalculationEvent;

import net.minecraft.server.level.ServerPlayer;

import nl.streats1.ancientextensions.config.ShinyCharmConfig;
import nl.streats1.ancientextensions.dex.ShinyCharmService;

public final class ShinyCharmCobblemonHooks {

    private ShinyCharmCobblemonHooks() {
    }

    public static void register() {
        CobblemonEvents.SHINY_CHANCE_CALCULATION.subscribe(ShinyCharmCobblemonHooks::onShinyChanceCalculation);
    }

    private static void onShinyChanceCalculation(ShinyChanceCalculationEvent event) {
        event.addModificationFunction(ShinyCharmCobblemonHooks::applyCharmMultiplier);
    }

    private static float applyCharmMultiplier(float rate, ServerPlayer player, com.cobblemon.mod.common.pokemon.Pokemon pokemon) {
        if (player == null || !ShinyCharmService.hasActiveBonus(player)) {
            return rate;
        }
        return rate / ShinyCharmConfig.rateMultiplier();
    }
}
