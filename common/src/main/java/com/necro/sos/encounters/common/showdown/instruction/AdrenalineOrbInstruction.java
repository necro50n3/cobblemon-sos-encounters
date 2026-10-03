package com.necro.sos.encounters.common.showdown.instruction;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction;
import kotlin.Unit;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class AdrenalineOrbInstruction implements InterpreterInstruction {
    private final String name;
    private final boolean success;

    public AdrenalineOrbInstruction(BattleMessage message) {
        this.name = message.argumentAt(0);
        this.success = Boolean.parseBoolean(message.argumentAt(1));
    }

    @Override
    public void invoke(@NotNull PokemonBattle battle) {
        battle.dispatchWaiting(1F, () -> {
            Component message = Component.translatable("sosencounters.battle.adrenaline_orb.item", this.name);
            battle.broadcastChatMessage(message);
            return Unit.INSTANCE;
        });

        battle.dispatchWaiting(1F, () -> {
            Component message = this.success
                ? Component.translatable("sosencounters.battle.adrenaline_orb.use")
                : Component.translatable("cobblemon.battle.fail");
            battle.broadcastChatMessage(message);
            return Unit.INSTANCE;
        });
    }
}
