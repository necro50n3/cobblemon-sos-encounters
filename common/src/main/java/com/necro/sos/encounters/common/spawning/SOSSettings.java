package com.necro.sos.encounters.common.spawning;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.api.pokemon.evolution.PreEvolution;
import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.config.ConfigCache;
import com.necro.sos.encounters.common.util.DoubleWeightedRandomMap;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record SOSSettings(
    PokemonProperties properties,
    Double callRate,
    DoubleWeightedRandomMap<PokemonProperties> spawnWeights,
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
        this(pokemon.createPokemonProperties(EXTRACTOR), null, new DoubleWeightedRandomMap<>(), SOSEncounters.CONFIG.SPAWNING.default_level_offset);
        this.addDefaultSpawn(pokemon);
    }

    public @Nullable String species() {
        return this.properties.getSpecies();
    }

    public double baseCallRate(FormData form) {
        return this.callRate != null ? this.callRate : ConfigCache.callRate(form.getCatchRate());
    }

    public PokemonProperties randomSpawn(Pokemon pokemon, RandomSource random) {
        if (this.spawnWeights.isEmpty()) this.addDefaultSpawn(pokemon);
        return this.spawnWeights.getRandom(random).orElseThrow();
    }

    private static DoubleWeightedRandomMap<PokemonProperties> fromStringMap(Map<String, Double> map) {
        DoubleWeightedRandomMap<PokemonProperties> spawnWeights = new DoubleWeightedRandomMap<>();
        if (map == null || map.isEmpty()) return spawnWeights;
        map.forEach((species, weight) -> spawnWeights.add(PokemonProperties.Companion.parse(species), weight));
        return spawnWeights;
    }

    private void addDefaultSpawn(Pokemon pokemon) {
        PreEvolution preEvolution = null;
        PokemonProperties spawnProperties = pokemon.createPokemonProperties(EXTRACTOR);
        for (
            PreEvolution current = pokemon.getPreEvolution();
            current != null && current.getForm().getLabels().contains(CobblemonPokemonLabels.BABY);
            current = current.getForm().getPreEvolution()
        ) {
            preEvolution = current;
        }
        if (preEvolution != null) {
            spawnProperties.setSpecies(preEvolution.getSpecies().getResourceIdentifier().getPath());
            spawnProperties.setForm(preEvolution.getForm().getName());
        }
        this.spawnWeights.add(spawnProperties, 1.0);
    }

    public record LevelOffset(Integer min, Integer max) {
        public LevelOffset {
            if (min == null) throw new IllegalArgumentException("Missing required key \"min\"");
            else if (max == null) throw new IllegalArgumentException("Missing required key \"max\"");
        }
    }
}
