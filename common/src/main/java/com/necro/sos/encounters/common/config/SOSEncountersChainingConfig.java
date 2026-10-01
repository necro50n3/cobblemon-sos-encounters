package com.necro.sos.encounters.common.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.LinkedHashMap;
import java.util.Map;

public class SOSEncountersChainingConfig implements ConfigData {
    @Comment("Chaining thresholds for IV floors")
    public Map<Integer, Integer> iv_thresholds = new LinkedHashMap<>();
    {
        iv_thresholds.put(5, 1);
        iv_thresholds.put(10, 2);
        iv_thresholds.put(20, 3);
        iv_thresholds.put(30, 4);
    }

    @Comment("Chaining thresholds for number of shiny rolls per spawn")
    public Map<Integer, Integer> shiny_thresholds = new LinkedHashMap<>();
    {
        shiny_thresholds.put(5, 1);
        shiny_thresholds.put(11, 5);
        shiny_thresholds.put(21, 9);
        shiny_thresholds.put(31, 13);
    }

    @Comment("Chaining thresholds for hidden ability chances")
    public Map<Integer, Double> ha_thresholds = new LinkedHashMap<>();
    {
        ha_thresholds.put(5, 0.00);
        ha_thresholds.put(10, 0.05);
        ha_thresholds.put(20, 0.10);
        ha_thresholds.put(30, 0.15);
    }
}
