package com.necro.sos.encounters.common.showdown.instruction;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction;
import kotlin.Unit;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class AdrenalineOrbInstruction implements InterpreterInstruction {
    @Override
    public void invoke(@NotNull PokemonBattle battle) {
        battle.dispatchWaiting(1F, () -> {
            Component message = Component.translatable("sosencounters.battle.adrenaline_orb.use");
            battle.broadcastChatMessage(message);
            return Unit.INSTANCE;
        });
    }
}
