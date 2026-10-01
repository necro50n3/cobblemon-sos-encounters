package com.necro.sos.encounters.common.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name="sosencounters-common")
public class SOSEncountersConfig implements ConfigData {
    public SOSEncountersChainingConfig CHAINING = new SOSEncountersChainingConfig();
    public SOSEncountersSpawningConfig SPAWNING = new SOSEncountersSpawningConfig();
}
