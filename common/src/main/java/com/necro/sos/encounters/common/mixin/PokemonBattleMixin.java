package com.necro.sos.encounters.common.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.necro.sos.encounters.common.util.IAdrenalineHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PokemonBattle.class)
public class PokemonBattleMixin implements IAdrenalineHolder {
    @Unique
    boolean sos_usedAdrenaline = false;

    @Override
    public boolean sos_hasUsedAdrenaline() {
        return this.sos_usedAdrenaline;
    }

    @Override
    public void sos_setAdrenalineUsed(boolean used) {
        this.sos_usedAdrenaline = used;
    }
}
