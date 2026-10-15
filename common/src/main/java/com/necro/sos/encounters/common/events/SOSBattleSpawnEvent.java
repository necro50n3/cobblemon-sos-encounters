package com.necro.sos.encounters.common.events;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.Cancelable;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class SOSBattleSpawnEvent {
    public static class Pre extends Cancelable {
        private final ServerPlayer player;
        private final @NotNull PokemonEntity source;
        private final PokemonBattle battle;

        public Pre(ServerPlayer player, @NotNull PokemonEntity source, PokemonBattle battle) {
            this.player = player;
            this.source = source;
            this.battle = battle;
        }

        public ServerPlayer player() {
            return this.player;
        }

        public @NotNull PokemonEntity source() {
            return this.source;
        }

        public PokemonBattle battle() {
            return this.battle;
        }
    }

    public record Post(ServerPlayer player, @NotNull PokemonEntity source, @NotNull PokemonEntity spawn, PokemonBattle battle) {}
}
