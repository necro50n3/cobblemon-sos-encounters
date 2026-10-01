package com.necro.sos.encounters.common.util;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SOSEncountersUtils {
    public static boolean isSOSBattle(@NotNull PokemonBattle battle) {
        return battle.getFormat().getBattleType().getName().equals("multi")
            && Arrays.stream(battle.getSide2().getActors()).allMatch(actor -> actor.getType() == ActorType.WILD);
    }
}
