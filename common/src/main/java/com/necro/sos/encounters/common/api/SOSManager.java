package com.necro.sos.encounters.common.api;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.ShinyChanceCalculationEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility;
import com.necro.sos.encounters.common.config.ConfigCache;
import com.necro.sos.encounters.common.spawning.SOSSettings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.List;

public class SOSManager {
    private final Pokemon pokemon;
    private final SOSSettings settings;
    private final RandomSource random;
    private int chain;

    private boolean hasCalled;
    private boolean notAnswered;

    public SOSManager(PokemonEntity pokemonEntity) {
        this.pokemon = pokemonEntity.getPokemon();
        SOSSettings settings = ConfigCache.spawnOverride(pokemonEntity);
        this.settings = settings != null ? settings : new SOSSettings(pokemonEntity.getPokemon());
        this.random = pokemonEntity.getRandom();
        this.chain = 0;

        this.hasCalled = false;
        this.notAnswered = false;
    }

    public SOSResult rollCall(float callChance, float spawnChance) {
        double base = this.settings.baseCallRate(this.pokemon.getForm());
        if (this.random.nextFloat() >= base * callChance) {
            this.hasCalled = false;
            return SOSResult.NONE;
        }
        this.hasCalled = true;
        if (this.random.nextFloat() >= base * spawnChance) {
            this.notAnswered = true;
            return SOSResult.CALL;
        }
        else {
            this.chain++;
            this.notAnswered = false;
            return SOSResult.SPAWN;
        }
    }

    public Pokemon rollSpawn(Pokemon basePokemon, ServerPlayer player) {
        Pokemon pokemon = this.settings.randomSpawn(basePokemon, this.random).create(player);
        int levelOffset = this.random.nextInt(this.settings.levelOffset().min(), this.settings.levelOffset().max() + 1);
        int level = Math.clamp(pokemon.getLevel() + levelOffset, 1, 100);
        pokemon.setLevel(level);
        this.rollStats(pokemon, player);
        return pokemon;
    }

    public void rollStats(Pokemon pokemon, ServerPlayer player) {
        this.rollIvs(pokemon);
        this.rollShiny(pokemon, player);
        this.rollHa(pokemon);
    }

    private void rollIvs(Pokemon pokemon) {
        IVs ivs = IVs.createRandomIVs(ConfigCache.minIvs(this.chain));
        pokemon.setIvs$common(ivs);
    }

    private void rollShiny(Pokemon pokemon, ServerPlayer player) {
        int rolls = ConfigCache.shinyRolls(this.chain);
        for (int i = 0; i < rolls; i++) {
            ShinyChanceCalculationEvent event = new ShinyChanceCalculationEvent(Cobblemon.config.getShinyRate(), pokemon);
            CobblemonEvents.SHINY_CHANCE_CALCULATION.post(event);
            float shinyRate = event.calculate(player);
            if (shinyRate > 0 && (this.random.nextFloat() < 1 / shinyRate)) {
                pokemon.setShiny(true);
                return;
            }
        }
        pokemon.setShiny(false);
    }

    private void rollHa(Pokemon pokemon) {
        if (this.random.nextDouble() >= ConfigCache.haRate(this.chain)) {
            pokemon.rollAbility();
            return;
        }
        pokemon.getForm().getAbilities().getMapping().values().forEach(
            abilities -> {
                List<HiddenAbility> hidden = abilities.stream()
                    .filter(a -> a instanceof HiddenAbility)
                    .map(a -> (HiddenAbility) a)
                    .toList();
                if (hidden.isEmpty()) return;
                HiddenAbility chosen = hidden.get(this.random.nextInt(hidden.size()));
                pokemon.setAbility$common(chosen.getTemplate().create(false, chosen.getPriority()));
            }
        );
    }

    public float multiplier() {
        float multiplier = 1.0F;
        if (this.hasCalled) multiplier *= 1.5F;
        if (this.notAnswered) multiplier *= 3.0F;
        return multiplier;
    }
}
