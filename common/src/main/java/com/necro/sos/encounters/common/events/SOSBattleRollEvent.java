package com.necro.sos.encounters.common.events;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SOSBattleRollEvent {
    private final PokemonEntity source;
    private double rate;

    public SOSBattleRollEvent(PokemonEntity source, double rate) {
        this.source = source;
        this.rate = rate;
    }

    public PokemonEntity source() {
        return this.source;
    }

    public double rate() {
        return this.rate;
    }

    public void set(double rate) {
        this.rate = rate;
    }

    public void add(float mod) {
        this.rate = Mth.clamp(this.rate + mod, 0F, 1F);
    }

    public void multiply(float mod) {
        this.rate = Mth.clamp(this.rate * mod, 0F, 1F);
    }

    public boolean roll(RandomSource random) {
        return random.nextDouble() < this.rate;
    }
}
