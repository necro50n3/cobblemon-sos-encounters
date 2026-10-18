package com.necro.sos.encounters.common.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.necro.sos.encounters.common.api.SOSManager;
import com.necro.sos.encounters.common.util.ISOSCaller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Pokemon.class)
public abstract class PokemonMixin implements ISOSCaller {
    @Unique
    private SOSManager sos_sosManager = null;

    @Shadow
    public abstract PokemonEntity getEntity();

    @Override
    public SOSManager sos_getSOSManager() {
        return this.sos_sosManager;
    }

    @Override
    public void sos_setSOSManager(SOSManager manager) {
        this.sos_sosManager = manager;
    }

    @Override
    public void sos_initSOSManager() {
        if (this.sos_sosManager == null) this.sos_sosManager = new SOSManager(this.getEntity());
    }
}
