package com.necro.sos.encounters.neoforge;

import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.neoforge.item.SOSEncountersCreativeTab;
import com.necro.sos.encounters.neoforge.item.SOSEncountersItemsNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(SOSEncounters.MODID)
public class SOSEncountersNeoForge {
    public SOSEncountersNeoForge(IEventBus modBus, ModContainer container) {
        SOSEncounters.init();

        SOSEncountersItemsNeoForge.registerItems(modBus);
        SOSEncountersCreativeTab.registerCreativeTab(modBus);
    }
}
