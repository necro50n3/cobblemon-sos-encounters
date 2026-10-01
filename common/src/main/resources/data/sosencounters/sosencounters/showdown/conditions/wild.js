{
    name: "wild",
    onResidualOrder: 30,
    onStart(target, source, sourceEffect) {
        target.sosMultiplier = 1.0;
        target.wasHitSuperEffective = false;
        if (target.battle.adrenaline === null) target.battle.adrenaline = false;
    },
    onHit(target, source, move) {
        if (target.runEffectiveness(move) > 1) target.wasHitSuperEffective = true;
    },
    onResidual(pokemon) {
        if (!pokemon.side.allySide || pokemon.side.allySide.pokemon.filter(p => p.hp).length > 0 || pokemon.status || !pokemon.hp || pokemon.volatiles["twoturnmove"]) {
            pokemon.sosMultiplier = 1.0;
            pokemon.wasHitSuperEffective = false;
            return;
        }
        else if (pokemon.calledSOS && !pokemon.battle.adrenaline) {
            pokemon.sosMultiplier = 1.0;
            pokemon.wasHitSuperEffective = false;
            return;
        }

        var callChance = 1.0;
        if (pokemon.hp < 0.2) callChance *= 5.0;
        else if (pokemon.hp < 0.5) callChance *= 3.0;
        if (pokemon.battle.adrenaline) callChance *= 2.0;

        if (pokemon.wasHitSuperEffective) pokemon.sosMultiplier *= 2.0;

        var abilityFactor = false;
        for (const foeActive of pokemon.side.foes()) {
            if (["intimidate", "unnerve", "pressure"].includes(foeActive.ability)) abilityFactor = true;
        }
        if (abilityFactor) pokemon.sosMultiplier *= 1.2;

        pokemon.battle.add("-sos", pokemon, pokemon.side.n + 1, callChance, pokemon.sosMultiplier);
        pokemon.calledSOS = true;
        pokemon.sosMultiplier = 1.0;
        pokemon.wasHitSuperEffective = false;
    }
}