package com.necro.sos.encounters.neoforge.events;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnPool;
import com.necro.asymmetric.battles.neoforge.reloader.BattleSpawnReloadListener;
import com.necro.sos.encounters.common.config.ConfigCache;
import com.necro.sos.encounters.common.item.SOSEncountersItems;
import com.necro.sos.encounters.common.spawning.SOSBattleSpawnPool;
import com.necro.sos.encounters.neoforge.showdown.StatusEffectsReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

public class SOSEncountersEvents {
    @SubscribeEvent
    private static void onReloadDataPack(AddReloadListenerEvent event) {
        event.addListener(new StatusEffectsReloadListener());
        event.addListener(new BattleSpawnReloadListener("sos", BattleSpawnPool.GSON, SOSBattleSpawnPool.class));
    }

    @SubscribeEvent
    private static void onServerStarting(ServerStartingEvent event) {
        ConfigCache.init();
        CobblemonHeldItemManager.INSTANCE.registerRemap(SOSEncountersItems.ADRENALINE_ORB.value(), "adrenalineorb");
    }

    @SubscribeEvent
    private static void onServerStarted(ServerStartedEvent event) {
        ConfigCache.onServerStarted();
    }
}
