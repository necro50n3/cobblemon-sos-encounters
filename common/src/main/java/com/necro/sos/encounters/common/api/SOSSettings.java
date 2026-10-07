package com.necro.sos.encounters.common.api;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.config.ConfigCache;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SOSSettings(
    PokemonProperties properties,
    Double callRate,
    Map<PokemonProperties, Double> spawnWeights,
    LevelOffset levelOffset
) {
    private static final List<PokemonPropertyExtractor> EXTRACTOR = List.of(
        PokemonPropertyExtractor.SPECIES,
        PokemonPropertyExtractor.FORM
    );

    public SOSSettings {
        if (levelOffset == null) levelOffset = SOSEncounters.CONFIG.SPAWNING.default_level_offset;
    }

    public SOSSettings(String properties, Double callRate, Map<String, Double> spawnWeights, LevelOffset levelOffset) {
        this(PokemonProperties.Companion.parse(properties), callRate, fromStringMap(spawnWeights), levelOffset);
    }

    public SOSSettings(Pokemon pokemon) {
        this(pokemon.createPokemonProperties(EXTRACTOR), null, new HashMap<>(), SOSEncounters.CONFIG.SPAWNING.default_level_offset);
    }

    public @Nullable String species() {
        return this.properties.getSpecies();
    }

    public double baseCallRate(FormData form) {
        return this.callRate != null ? this.callRate : ConfigCache.callRate(form.getCatchRate());
    }

    private static Map<PokemonProperties, Double> fromStringMap(Map<String, Double> map) {
        Map<PokemonProperties, Double> spawnWeights = new HashMap<>();
        if (map == null || map.isEmpty()) return spawnWeights;
        map.forEach((species, weight) -> spawnWeights.put(PokemonProperties.Companion.parse(species), weight));
        return spawnWeights;
    }

    public record LevelOffset(Integer min, Integer max) {
        public LevelOffset {
            if (min == null) throw new IllegalArgumentException("Missing required key \"min\"");
            else if (max == null) throw new IllegalArgumentException("Missing required key \"max\"");
        }
    }
}
