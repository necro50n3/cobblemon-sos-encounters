package com.necro.sos.encounters.fabric;

import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.fabric.events.SOSEncountersEvents;
import com.necro.sos.encounters.fabric.item.SOSEncountersCreativeTab;
import com.necro.sos.encounters.fabric.item.SOSEncountersItemsFabric;
import net.fabricmc.api.ModInitializer;

public class SOSEncountersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SOSEncounters.init();

        SOSEncountersItemsFabric.registerItems();
        SOSEncountersCreativeTab.registerCreativeTab();
        SOSEncountersEvents.init();
    }

}
