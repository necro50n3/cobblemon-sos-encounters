package com.necro.sos.encounters.common.config;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnDetail;
import com.necro.asymmetric.battles.common.config.serializer.Indent;
import com.necro.asymmetric.battles.common.config.serializer.YamlKey;
import com.necro.asymmetric.battles.common.registry.SpawnPoolTypeRegistry;
import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.spawning.SOSBattleSpawnPool;
import kotlin.ranges.IntRange;

import java.util.Map;

@Indent(false)
public class SOSSettingsAdapter {
    private String properties;
    @YamlKey("call_rate") private Double callRate;
    @YamlKey("spawn_weights") final private Map<String, Double> spawnWeights = Map.of();
    @YamlKey("level_offset") final private LevelOffset levelOffset = new LevelOffset(-5, 0);

    public void registerSpawnDetails() {
        String error = null;
        if (this.properties == null) error = "Failed to parse Spawn Overrides entry: Missing required key \"properties\".";
        if (this.levelOffset.min() == null) error = String.format("Failed to parse %s: Missing required key \"min\"", this.properties);
        if (this.levelOffset.max() == null) error = String.format("Failed to parse %s: Missing required key \"max\"", this.properties);
        if (error != null) {
            SOSEncounters.LOGGER.error(error);
            return;
        }

        PokemonProperties spawnProperties = PokemonProperties.Companion.parse(this.properties);
        IntRange levelRangeOffset = new IntRange(this.levelOffset.min(), this.levelOffset.max());
        this.spawnWeights.forEach((properties, weight) -> {
            if (properties == null) {
                SOSEncounters.LOGGER.error("Failed to parse spawn for {}: Missing required key \"properties\"", this.properties);
                return;
            }

            BattleSpawnDetail detail = BattleSpawnDetail.basic(PokemonProperties.Companion.parse(properties), levelRangeOffset, weight);
            SpawnPoolTypeRegistry.register("sos", spawnProperties, detail, SOSBattleSpawnPool.class);
        });

        SOSBattleSpawnPool pool = (SOSBattleSpawnPool) SpawnPoolTypeRegistry.get("sos", spawnProperties);
        if (pool != null) pool.callRate = this.callRate;
    }

    public record LevelOffset(Integer min, Integer max) {}
}

