package com.necro.sos.encounters.common.spawning;

import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnPool;
import com.necro.asymmetric.battles.common.util.PropertyExtractors;
import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.config.ConfigCache;
import kotlin.ranges.IntRange;

public class SOSBattleSpawnPool extends BattleSpawnPool {
    public Double callRate = null;

    public double baseCallRate(FormData form) {
        return this.callRate != null ? this.callRate : ConfigCache.callRate(form.getCatchRate());
    }

    public static SOSBattleSpawnPool create(Pokemon pokemon) {
        SOSBattleSpawnPool pool = new SOSBattleSpawnPool();
        pool.properties = pokemon.createPokemonProperties(PropertyExtractors.LONG_EXTRACTOR);
        pool.properties.setAspects(pokemon.getAspects());
        pool.pokemon = pool.properties.getOriginalString();
        pool.spawns.add(BattleSpawnPool.defaultSpawn(pokemon, new IntRange(SOSEncounters.CONFIG.SPAWNING.default_level_offset.min(), SOSEncounters.CONFIG.SPAWNING.default_level_offset.max())));
        return pool;
    }
}
