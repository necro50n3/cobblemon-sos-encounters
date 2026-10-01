package com.necro.sos.encounters.common.showdown;

import com.cobblemon.mod.common.battles.ShowdownInterpreter;
import com.necro.sos.encounters.common.showdown.instruction.AdrenalineOrbInstruction;
import com.necro.sos.encounters.common.showdown.instruction.SOSInstruction;

public class SOSEncountersShowdownRegistry {
    public static void registerInstructions() {
        ShowdownInterpreter.registerUpdateInstructionParser("-sos", (battle, instruction, message, messageIterator) ->
            new SOSInstruction(battle, message)
        );
        ShowdownInterpreter.registerUpdateInstructionParser("-adrenalineorb", (battle, instruction, message, messageIterator) ->
            new AdrenalineOrbInstruction()
        );
    }
}
