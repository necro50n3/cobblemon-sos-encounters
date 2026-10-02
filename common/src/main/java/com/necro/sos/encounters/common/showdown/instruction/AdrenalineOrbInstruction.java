package com.necro.sos.encounters.common.showdown.instruction;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction;
import kotlin.Unit;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public record AdrenalineOrbInstruction(BattleMessage message) implements InterpreterInstruction {
    @Override
    public void invoke(@NotNull PokemonBattle battle) {
        battle.dispatchWaiting(1F, () -> {
            String name = message.argumentAt(0);
            Component message = Component.translatable("sosencounters.battle.adrenaline_orb.item", name);
            battle.broadcastChatMessage(message);
            return Unit.INSTANCE;
        });

        battle.dispatchWaiting(1F, () -> {
            Component message = Component.translatable("sosencounters.battle.adrenaline_orb.use");
            battle.broadcastChatMessage(message);
            return Unit.INSTANCE;
        });
    }
}
