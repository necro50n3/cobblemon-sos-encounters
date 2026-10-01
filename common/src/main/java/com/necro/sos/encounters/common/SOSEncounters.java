package com.necro.sos.encounters.common;

import com.mojang.logging.LogUtils;
import com.necro.sos.encounters.common.config.SOSEncountersConfig;
import com.necro.sos.encounters.common.config.serializer.PrimitiveYamlConfigSerializer;
import com.necro.sos.encounters.common.showdown.SOSEncountersShowdownRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import org.slf4j.Logger;

public class SOSEncounters {
    public static final String MODID = "sosencounters";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static SOSEncountersConfig CONFIG;

    public static void init() {
        LOGGER.info("Initiating {}", MODID);

        AutoConfig.register(SOSEncountersConfig.class, PrimitiveYamlConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(SOSEncountersConfig.class).getConfig();

        SOSEncountersShowdownRegistry.registerInstructions();
    }
}
