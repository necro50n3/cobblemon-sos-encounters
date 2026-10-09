package com.necro.sos.encounters.common.api;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.ShinyChanceCalculationEvent;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility;
import com.necro.asymmetric.battles.common.api.AsymmetricAPI;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnPool;
import com.necro.asymmetric.battles.common.registry.SpawnPoolTypeRegistry;
import com.necro.asymmetric.battles.common.util.PropertyExtractors;
import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.config.ConfigCache;
import com.necro.sos.encounters.common.spawning.SOSBattleSpawnPool;
import kotlin.ranges.IntRange;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.List;

public class SOSManager {
    private final Pokemon pokemon;
    private final PokemonProperties properties;
    private final SOSBattleSpawnPool pool;
    private final RandomSource random;
    private int chain;

    private boolean hasCalled;
    private boolean notAnswered;
    private PokemonEntity lastSpawn;

    public SOSManager(PokemonEntity pokemonEntity) {
        this.pokemon = pokemonEntity.getPokemon();
        this.properties = this.pokemon.createPokemonProperties(PropertyExtractors.LONG_EXTRACTOR);
        this.properties.setAspects(this.pokemon.getAspects());
        SOSBattleSpawnPool pool = (SOSBattleSpawnPool) SpawnPoolTypeRegistry.get("sos", pokemonEntity);
        this.pool = pool != null ? pool : SOSBattleSpawnPool.create(this.pokemon);
        this.random = pokemonEntity.getRandom();
        this.chain = 0;

        this.hasCalled = false;
        this.notAnswered = false;
        this.lastSpawn = null;
    }

    public SOSResult rollCall(float callMultiplier, float spawnChance) {
        double base = this.pool.baseCallRate(this.pokemon.getForm());
        double spawnMultiplier = this.multiplier();

        if (this.random.nextFloat() >= base * callMultiplier) {
            this.hasCalled = false;
            return SOSResult.NONE;
        }
        this.hasCalled = true;
        if (this.random.nextFloat() >= base * spawnChance * spawnMultiplier) {
            this.notAnswered = true;
            return SOSResult.CALL;
        }
        else {
            this.chain++;
            this.notAnswered = false;
            return SOSResult.SPAWN;
        }
    }

    public Pokemon rollSpawn(ServerPlayer player, PokemonEntity pokemonEntity, PokemonBattle battle) {
        Pokemon pokemon = AsymmetricAPI.getRandomBattleSpawn(
            this.pool,
            player,
            (ServerLevel) pokemonEntity.level(),
            pokemonEntity.blockPosition(),
            battle,
            this.properties,
            this.pokemon.getLevel(),
            () -> BattleSpawnPool.defaultSpawn(this.pokemon, new IntRange(SOSEncounters.CONFIG.SPAWNING.default_level_offset.min(), SOSEncounters.CONFIG.SPAWNING.default_level_offset.max())).create(this.pokemon.getLevel(), player)
        );
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

    public PokemonEntity lastSpawn() {
        return this.lastSpawn;
    }

    public void setLastSpawn(PokemonEntity pokemonEntity) {
        this.lastSpawn = pokemonEntity;
    }
}
