package com.necro.sos.encounters.fabric.events;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import com.necro.sos.encounters.common.config.ConfigCache;
import com.necro.sos.encounters.common.item.SOSEncountersItems;
import com.necro.sos.encounters.fabric.showdown.StatusEffectsReloadListener;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class SOSEncountersEvents {
    public static void init() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new StatusEffectsReloadListener());

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ConfigCache.init();
            CobblemonHeldItemManager.INSTANCE.registerRemap(SOSEncountersItems.ADRENALINE_ORB.value(), "adrenalineorb");
        });
    }
}
