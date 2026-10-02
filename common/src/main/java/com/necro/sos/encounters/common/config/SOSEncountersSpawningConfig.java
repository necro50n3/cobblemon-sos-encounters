package com.necro.sos.encounters.common.config;

import com.necro.sos.encounters.common.spawning.SOSSettings;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.LinkedHashMap;
import java.util.Map;

@Config(name="sosencounters/spawning")
public class SOSEncountersSpawningConfig implements ConfigData {
    @Comment("SOS Call rates based on the Pokémon's base catch rate (1 - 255)")
    public Map<Integer, Double> call_rates = new LinkedHashMap<>();
    {
        call_rates.put(1, 0.03);
        call_rates.put(50, 0.06);
        call_rates.put(100, 0.09);
        call_rates.put(200, 0.15);
    }

    @Comment("The default minimum and maximum level offset relative to the calling Pokémon")
    public SOSSettings.LevelOffset default_level_offset = new SOSSettings.LevelOffset(-5, 0);

    @Comment("A list of Pokémon labels to blacklist from encounters.")
    public String[] label_blacklist = { "legendary", "mythical", "ultra_beast", "paradox" };

    @Comment("A list of Pokémon properties to blacklist from encounters. Supports property strings such as \"rattata alolan\".")
    public String[] pokemon_blacklist = { "floette flower=eternal" };

    @Comment("A list of Pokémon aspects to blacklist from encounters.")
    public String[] aspects_blacklist = { "aspect=raid" };

    @Comment(
            """
            A list of SOS spawn overriders from Pokémon properties. Supports property strings such as "rattata alolan".
            SOS spawning will always be based on the original Pokémon at the start of the battle, not whichever Pokémon is currently active.
            \s
            Format:
            properties:     The Pokémon properties. Required.
            call_rate:      The base probability of the Pokémon making an SOS call. Default: Refer to the `call_rates` config value.
            spawn_weights:  A weighted list of Pokémon properties that can spawn from an SOS call. Default: The base, non-baby Pokémon of this evolutionary line.
            level_offset:   The minimum and maximum level offset relative to the calling Pokémon. Default: Refer to the `default_level_offset` config value.
            \s
            Example:
            -   properties: charizard
                call_rate: 0.03
                spawn_weights:
                    charmander: 10.0
                level_offset:
                    min: -5
                    max: 0
            """
    )
    public SOSSettingsAdapter[] spawn_overrides = {
        new SOSSettingsAdapter("nidoranf", Map.of("nidoranf", 1.0, "nidoranm", 1.0)),
        new SOSSettingsAdapter("nidoranm", Map.of("nidoranf", 1.0, "nidoranm", 1.0)),
        new SOSSettingsAdapter("nidorina", Map.of("nidoranf", 1.0, "nidoranm", 1.0)),
        new SOSSettingsAdapter("nidorino", Map.of("nidoranf", 1.0, "nidoranm", 1.0)),
        new SOSSettingsAdapter("nidoqueen", Map.of("nidoranf", 1.0, "nidoranm", 1.0)),
        new SOSSettingsAdapter("nidoking", Map.of("nidoranf", 1.0, "nidoranm", 1.0))
    };
}
