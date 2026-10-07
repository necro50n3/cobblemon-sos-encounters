package com.necro.sos.encounters.common.config;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.properties.AspectPropertyType;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnDetail;
import com.necro.asymmetric.battles.common.registry.SpawnRegistry;
import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.api.SOSSettings;
import com.necro.sos.encounters.common.spawning.SOSBattleSpawnPool;
import kotlin.ranges.IntRange;

import java.util.*;

public class ConfigCache {
    public static final List<PokemonPropertyExtractor> EXTRACTOR = List.of(
        PokemonPropertyExtractor.SPECIES,
        PokemonPropertyExtractor.ASPECTS,
        PokemonPropertyExtractor.SHINY,
        PokemonPropertyExtractor.FORM,
        PokemonPropertyExtractor.GENDER
    );

    private static final NavigableMap<Integer, Double> CALL_RATES = new TreeMap<>();
    private static final NavigableMap<Integer, Integer> IV_CHAIN_THRESHOLDS = new TreeMap<>();
    private static final NavigableMap<Integer, Integer> SHINY_CHAIN_THRESHOLDS = new TreeMap<>();
    private static final NavigableMap<Integer, Double> HA_CHAIN_THRESHOLDS = new TreeMap<>();

    private static final Map<String, List<PokemonProperties>> POKEMON_BLACKLIST = new HashMap<>();
    private static final Set<CustomPokemonProperty> ASPECT_BLACKLIST = new HashSet<>();

    public static void init() {
        if (!CALL_RATES.containsKey(0)) CALL_RATES.put(0, 0.0);
        if (!IV_CHAIN_THRESHOLDS.containsKey(0)) IV_CHAIN_THRESHOLDS.put(0, 0);
        if (!SHINY_CHAIN_THRESHOLDS.containsKey(0)) SHINY_CHAIN_THRESHOLDS.put(0, 1);
        if (!HA_CHAIN_THRESHOLDS.containsKey(0)) HA_CHAIN_THRESHOLDS.put(0, 0.0);

        CALL_RATES.putAll(SOSEncounters.CONFIG.SPAWNING.call_rates);
        IV_CHAIN_THRESHOLDS.putAll(SOSEncounters.CONFIG.CHAINING.iv_thresholds);
        SHINY_CHAIN_THRESHOLDS.putAll(SOSEncounters.CONFIG.CHAINING.shiny_thresholds);
        HA_CHAIN_THRESHOLDS.putAll(SOSEncounters.CONFIG.CHAINING.ha_thresholds);

        Arrays.stream(SOSEncounters.CONFIG.SPAWNING.pokemon_blacklist)
            .map(properties -> {
                PokemonProperties props = PokemonProperties.Companion.parse(properties, " ", "=");
                if (props.getSpecies() == null) {
                    SOSEncounters.LOGGER.warn("Invalid species in Pokémon blacklist: {}", properties);
                    return null;
                }
                return props;
            })
            .forEach(properties -> {
                if (properties == null) return;
                List<PokemonProperties> map = POKEMON_BLACKLIST.computeIfAbsent(properties.getSpecies().toLowerCase(Locale.ROOT), species -> new ArrayList<>());
                map.add(properties);
            });
        ASPECT_BLACKLIST.addAll(Arrays.stream(SOSEncounters.CONFIG.SPAWNING.aspects_blacklist)
            .map(AspectPropertyType.INSTANCE::fromString)
            .toList()
        );
    }

    public static void onServerStarted() {
        Arrays.stream(SOSEncounters.CONFIG.SPAWNING.spawn_overrides)
            .forEach(adapter -> {
                SOSSettings settings = adapter.toSettings();
                if (settings.species() == null) {
                    SOSEncounters.LOGGER.warn("Invalid species in spawn overrides: {}", adapter.properties());
                    return;
                }

                settings.spawnWeights().forEach((spawn, weight) -> {
                    BattleSpawnDetail detail = new BattleSpawnDetail();
                    detail.pokemon = settings.properties();
                    detail.weight = weight;
                    detail.levelRangeOffset = new IntRange(settings.levelOffset().min(), settings.levelOffset().max());
                    SpawnRegistry.register("sos", detail, SOSBattleSpawnPool.class);
                });

                SOSBattleSpawnPool pool = (SOSBattleSpawnPool) SpawnRegistry.get("sos", settings.properties());
                if (pool != null) pool.callRate = settings.callRate();
            });
    }

    public static boolean canSOS(PokemonEntity pokemonEntity) {
        if (isLabelBlacklisted(pokemonEntity)) return false;
        else if (isPropertyBlacklisted(pokemonEntity)) return false;
        else return !isAspectBlacklisted(pokemonEntity);
    }

    public static double callRate(int catchRate) {
        return CALL_RATES.floorEntry(catchRate).getValue();
    }

    public static int minIvs(int chain) {
        return IV_CHAIN_THRESHOLDS.floorEntry(chain).getValue();
    }

    public static int shinyRolls(int chain) {
        return SHINY_CHAIN_THRESHOLDS.floorEntry(chain).getValue();
    }

    public static double haRate(int chain) {
        return HA_CHAIN_THRESHOLDS.floorEntry(chain).getValue();
    }

    private static boolean isLabelBlacklisted(PokemonEntity pokemonEntity) {
        return Arrays.stream(SOSEncounters.CONFIG.SPAWNING.label_blacklist).anyMatch(label -> pokemonEntity.getPokemon().hasLabels(label));
    }

    private static boolean isPropertyBlacklisted(PokemonEntity pokemonEntity) {
        List<PokemonProperties> blacklist = POKEMON_BLACKLIST.get(pokemonEntity.getPokemon().getSpecies().getResourceIdentifier().getPath());
        if (blacklist == null) return false;
        PokemonProperties check = pokemonEntity.getPokemon().createPokemonProperties(EXTRACTOR);
        check.setAspects(pokemonEntity.getAspects());
        return blacklist.stream().anyMatch(properties -> properties.isSubSetOf(check) && check.getAspects().containsAll(properties.getAspects()));
    }

    private static boolean isAspectBlacklisted(PokemonEntity pokemonEntity) {
        return ASPECT_BLACKLIST.stream().anyMatch(aspect -> aspect.matches(pokemonEntity));
    }
}
