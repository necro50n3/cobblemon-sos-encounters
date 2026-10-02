package com.necro.sos.encounters.common.config;

import com.necro.sos.encounters.common.config.serializer.Indent;
import com.necro.sos.encounters.common.config.serializer.YamlKey;
import com.necro.sos.encounters.common.spawning.SOSSettings;

import java.util.Map;

@Indent(false)
public record SOSSettingsAdapter(
    String properties,
    @YamlKey("call_rate") Double callRate,
    @YamlKey("spawn_weights") Map<String, Double> spawnWeights,
    @YamlKey("level_offset") SOSSettings.LevelOffset levelOffset
) {
    public SOSSettingsAdapter {
        if (properties == null) throw new IllegalArgumentException("Missing required key \"properties\"");
    }

    public SOSSettingsAdapter(String properties) {
        this(properties, null, null, null);
    }

    public SOSSettingsAdapter(String properties, Map<String, Double> spawnWeights) {
        this(properties, null, spawnWeights, null);
    }

    public SOSSettingsAdapter(String properties, SOSSettings.LevelOffset levelOffset) {
        this(properties, null, null, levelOffset);
    }

    public SOSSettingsAdapter(String properties, Map<String, Double> spawnWeights, SOSSettings.LevelOffset levelOffset) {
        this(properties, null, spawnWeights, levelOffset);
    }

    public SOSSettings toSettings() {
        return new SOSSettings(this.properties, this.callRate, this.spawnWeights, this.levelOffset);
    }
}

