package com.necro.sos.encounters.fabric.events;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnPool;
import com.necro.asymmetric.battles.fabric.reloader.BattleSpawnReloadListener;
import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.config.ConfigCache;
import com.necro.sos.encounters.common.item.SOSEncountersItems;
import com.necro.sos.encounters.common.spawning.SOSBattleSpawnPool;
import com.necro.sos.encounters.fabric.showdown.StatusEffectsReloadListener;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class SOSEncountersEvents {
    public static void init() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new StatusEffectsReloadListener());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
            new BattleSpawnReloadListener(ResourceLocation.fromNamespaceAndPath(SOSEncounters.MODID, "sos"),
                "sos",
                BattleSpawnPool.GSON,
                SOSBattleSpawnPool.class
            ));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ConfigCache.init();
            CobblemonHeldItemManager.INSTANCE.registerRemap(SOSEncountersItems.ADRENALINE_ORB.value(), "adrenalineorb");
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> ConfigCache.onServerStarted());
    }
}
