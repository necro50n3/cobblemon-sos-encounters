package com.necro.sos.encounters.common;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import com.mojang.logging.LogUtils;
import com.necro.sos.encounters.common.config.SOSEncountersConfig;
import com.necro.sos.encounters.common.config.serializer.PrimitiveYamlConfigSerializer;
import com.necro.sos.encounters.common.showdown.SOSEncountersShowdownRegistry;
import com.necro.sos.encounters.common.util.ISOSCaller;
import com.necro.sos.encounters.common.util.SOSEncountersUtils;
import me.shedaniel.autoconfig.AutoConfig;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Consumer;

public class SOSEncounters {
    public static final String MODID = "sosencounters";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static SOSEncountersConfig CONFIG;

    public static void init() {
        LOGGER.info("Initiating {}", MODID);

        AutoConfig.register(SOSEncountersConfig.class, PrimitiveYamlConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(SOSEncountersConfig.class).getConfig();

        SOSEncountersShowdownRegistry.registerInstructions();

        CobblemonEvents.BATTLE_FLED.subscribe(Priority.LOWEST, (Consumer<BattleFledEvent>) event -> despawnSOSOnFlee(event.getBattle()));
        PlatformEvents.SERVER_PLAYER_LOGOUT.subscribe(Priority.HIGHEST, event -> {
            PokemonBattle battle = BattleRegistry.getBattleByParticipatingPlayer(event.getPlayer());
            if (battle != null) despawnSOSOnFlee(battle);
        });
    }

    private static void despawnSOSOnFlee(PokemonBattle battle) {
        if (!SOSEncountersUtils.isSOSBattle(battle)) return;
        List<PokemonEntity> entities = battle.getSide2().getActivePokemon().stream()
            .filter(pokemon -> pokemon.getBattlePokemon() != null && pokemon.isAlive())
            .map(pokemon -> pokemon.getBattlePokemon().getEntity())
            .filter(pokemon -> pokemon != null && ((ISOSCaller) pokemon).sos_getSOSManager() != null)
            .toList();
        if (entities.size() < 2) return;
        ((ISOSCaller) entities.getFirst()).sos_getSOSManager().lastSpawn().discard();
    }
}
