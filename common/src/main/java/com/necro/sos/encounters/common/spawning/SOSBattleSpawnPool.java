package com.necro.sos.encounters.common.spawning;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.api.pokemon.evolution.PreEvolution;
import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnDetail;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnPool;
import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.config.ConfigCache;
import kotlin.ranges.IntRange;

import java.util.List;

public class SOSBattleSpawnPool extends BattleSpawnPool {
    private static final List<PokemonPropertyExtractor> EXTRACTOR = List.of(
        PokemonPropertyExtractor.SPECIES,
        PokemonPropertyExtractor.FORM
    );

    public Double callRate = null;

    public double baseCallRate(FormData form) {
        return this.callRate != null ? this.callRate : ConfigCache.callRate(form.getCatchRate());
    }

    public static SOSBattleSpawnPool create(Pokemon pokemon) {
        SOSBattleSpawnPool pool = new SOSBattleSpawnPool();
        pool.pokemon = pokemon.createPokemonProperties(ConfigCache.EXTRACTOR);
        pool.spawns.add(defaultSpawn(pokemon));
        return pool;
    }

    protected static BattleSpawnDetail defaultSpawn(Pokemon pokemon) {
        PreEvolution preEvolution = null;
        PokemonProperties spawnProperties = pokemon.createPokemonProperties(EXTRACTOR);
        for (
            PreEvolution current = pokemon.getPreEvolution();
            current != null && !current.getForm().getLabels().contains(CobblemonPokemonLabels.BABY);
            current = current.getForm().getPreEvolution()
        ) {
            preEvolution = current;
        }
        if (preEvolution != null) {
            spawnProperties.setSpecies(preEvolution.getSpecies().getResourceIdentifier().getPath());
            spawnProperties.setForm(preEvolution.getForm().getName());
        }
        return BattleSpawnDetail.basic(spawnProperties, new IntRange(SOSEncounters.CONFIG.SPAWNING.default_level_offset.min(), SOSEncounters.CONFIG.SPAWNING.default_level_offset.max()), 1.0);
    }
}
