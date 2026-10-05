package com.necro.sos.encounters.common.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.necro.sos.encounters.common.api.SOSManager;
import com.necro.sos.encounters.common.util.ISOSCaller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PokemonEntity.class)
public class PokemonEntityMixin implements ISOSCaller {
    @Unique
    private SOSManager sos_sosManager = null;
    @Unique
    private boolean sos_isSOSSpawn = false;

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
        if (this.sos_sosManager == null) this.sos_sosManager = new SOSManager((PokemonEntity) (Object) this);
    }

    @Override
    public boolean sos_isSOSSpawn() {
        return this.sos_isSOSSpawn;
    }

    @Override
    public void sos_setSOSSpawn() {
        this.sos_isSOSSpawn = true;
    }
}
