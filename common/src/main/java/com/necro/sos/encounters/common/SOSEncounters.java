package com.necro.sos.encounters.common;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.stats.BattleEvSource;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.mojang.logging.LogUtils;
import com.necro.asymmetric.battles.common.config.serializer.PrimitiveYamlConfigSerializer;
import com.necro.asymmetric.battles.common.util.IBattleSpawn;
import com.necro.sos.encounters.common.config.SOSEncountersConfig;
import com.necro.sos.encounters.common.showdown.SOSEncountersShowdownRegistry;
import com.necro.sos.encounters.common.util.ISOSCaller;
import com.necro.sos.encounters.common.util.SOSEncountersUtils;
import me.shedaniel.autoconfig.AutoConfig;
import org.slf4j.Logger;

import java.util.List;

public class SOSEncounters {
    public static final String MODID = "sosencounters";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static SOSEncountersConfig CONFIG;

    public static void init() {
        LOGGER.info("Initiating {}", MODID);

        AutoConfig.register(SOSEncountersConfig.class, PrimitiveYamlConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(SOSEncountersConfig.class).getConfig();

        SOSEncountersShowdownRegistry.registerInstructions();

        CobblemonEvents.EV_GAINED_EVENT_PRE.subscribe(Priority.LOWEST, event -> {
            if (!event.getSource().isBattle()) return;
            PokemonBattle battle = ((BattleEvSource) event.getSource()).getBattle();
            if (!SOSEncountersUtils.isSOSBattle(battle)) return;
            if (((ISOSCaller) event.getSource().getPokemon()).sos_getSOSManager() == null) return;
            event.cancel();
        });

        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOWEST, event -> {
            if (!SOSEncountersUtils.isSOSBattle(event.getBattle())) return;
            else if (((ISOSCaller) event.getKilled().getEffectedPokemon()).sos_getSOSManager() == null) return;
            else if ((int) event.getBattle().getSide2().getActivePokemon().stream().filter(pokemon -> pokemon.getBattlePokemon() != null && pokemon.getBattlePokemon().getHealth() > 0).count() == 0) return;
            for (ActiveBattlePokemon active : event.getBattle().getSide1().getActivePokemon()) {
                BattlePokemon killer = active.getBattlePokemon();
                if (killer == null) continue;
                int multiplier = ((IBattleSpawn) event.getKilled().getEffectedPokemon()).aba_isBattleSpawn() && CONFIG.CHAINING.ev_bonus ? 2 : 1;
                Cobblemon.INSTANCE.getEvYieldCalculator().calculate(killer, event.getKilled()).forEach((stat, amount) ->
                    killer.getEffectedPokemon().getEvs().add(stat, amount * multiplier, new BattleEvSource(event.getBattle(), List.of(event.getKilled()), killer.getEffectedPokemon()))
                );
            }
        });
    }
}
