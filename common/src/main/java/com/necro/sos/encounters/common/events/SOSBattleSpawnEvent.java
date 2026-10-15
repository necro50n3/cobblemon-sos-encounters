package com.necro.sos.encounters.common.events;

import com.cobblemon.mod.common.api.events.Cancelable;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import org.jetbrains.annotations.NotNull;

public class SOSBattleSpawnEvent {
    public static class Pre extends Cancelable {
        private final @NotNull PokemonEntity source;

        public Pre(@NotNull PokemonEntity source) {
            this.source = source;
        }

        public @NotNull PokemonEntity source() {
            return this.source;
        }
    }

    public record Post(@NotNull PokemonEntity source, @NotNull PokemonEntity spawn) {}
}
