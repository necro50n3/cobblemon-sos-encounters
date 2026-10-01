package com.necro.sos.encounters.common.spawning;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.api.pokemon.evolution.PreEvolution;
import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.necro.sos.encounters.common.config.ConfigCache;
import com.necro.sos.encounters.common.util.DoubleWeightedRandomMap;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record SOSSettings(
    PokemonProperties properties,
    Double callChance,
    DoubleWeightedRandomMap<PokemonProperties> spawnWeights,
    LevelOffset levelOffset
) {
    private static final List<PokemonPropertyExtractor> EXTRACTOR = List.of(
        PokemonPropertyExtractor.SPECIES,
        PokemonPropertyExtractor.FORM
    );

    public SOSSettings(String properties, Double callChance, Map<String, Double> spawnWeights, LevelOffset levelOffset) {
        this(PokemonProperties.Companion.parse(properties), callChance, fromStringMap(spawnWeights), levelOffset);
    }

    public SOSSettings(Pokemon pokemon) {
        this(pokemon.createPokemonProperties(EXTRACTOR), null, new DoubleWeightedRandomMap<>(), new LevelOffset(-5, 0));
        PreEvolution preEvolution = null;
        for (
            PreEvolution current = pokemon.getPreEvolution();
            current != null && current.getForm().getLabels().contains(CobblemonPokemonLabels.BABY);
            current = current.getForm().getPreEvolution()
        ) {
            preEvolution = current;
        }
        if (preEvolution != null) {
            this.properties.setSpecies(preEvolution.getSpecies().getResourceIdentifier().getPath());
            this.properties.setForm(preEvolution.getForm().getName());
        }
        this.spawnWeights.add(pokemon.createPokemonProperties(EXTRACTOR), 1.0);
    }

    public @Nullable String species() {
        return this.properties.getSpecies();
    }

    public double baseCallRate(Species species) {
        return this.callChance != null ? this.callChance : ConfigCache.callRate(species.getCatchRate());
    }

    public PokemonProperties randomSpawn(RandomSource random) {
        return this.spawnWeights.getRandom(random).orElseThrow();
    }

    private static DoubleWeightedRandomMap<PokemonProperties> fromStringMap(Map<String, Double> map) {
        DoubleWeightedRandomMap<PokemonProperties> spawnWeights = new DoubleWeightedRandomMap<>();
        if (map == null || map.isEmpty()) return spawnWeights;
        map.forEach((species, weight) -> spawnWeights.add(PokemonProperties.Companion.parse(species), weight));
        return spawnWeights;
    }

    public record LevelOffset(Integer min, Integer max) {
        public LevelOffset {
            if (min == null) throw new IllegalArgumentException("Missing required key \"min\"");
            else if (max == null) throw new IllegalArgumentException("Missing required key \"max\"");
        }
    }
}
