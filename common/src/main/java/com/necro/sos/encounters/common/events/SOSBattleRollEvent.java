package com.necro.sos.encounters.common.events;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SOSBattleRollEvent {
    private final ServerPlayer player;
    private final PokemonEntity source;
    private double rate;

    public SOSBattleRollEvent(ServerPlayer player, PokemonEntity source, double rate) {
        this.player = player;
        this.source = source;
        this.rate = rate;
    }

    public ServerPlayer player() {
        return this.player;
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
