{
    name: "wild",
    onResidualOrder: 30,
    onStart(target, source, sourceEffect) {
        target.wasHitSuperEffective = false;
        if (target.battle.adrenaline === null) target.battle.adrenaline = false;
        if (target.battle.calledSOS === null) target.battle.calledSOS = false;
    },
    onHit(target, source, move) {
        if (target.runEffectiveness(move) > 1) target.wasHitSuperEffective = true;
    },
    onResidual(pokemon) {
        if (!pokemon.side.allySide || pokemon.side.allySide.pokemon.filter(p => p.hp).length > 0 || pokemon.status || !pokemon.hp || pokemon.volatiles["twoturnmove"]) {
            pokemon.wasHitSuperEffective = false;
            return;
        }
        else if (pokemon.battle.calledSOS && !pokemon.battle.adrenaline) {
            pokemon.wasHitSuperEffective = false;
            return;
        }

        var callMultiplier = 1.0;
        var hpRatio = pokemon.hp / pokemon.maxhp;
        if (hpRatio < 0.2) callMultiplier *= 5.0;
        else if (hpRatio < 0.5) callMultiplier *= 3.0;
        if (pokemon.battle.adrenaline) callMultiplier *= 2.0;

        var spawnMultiplier = 1.0;
        var abilityFactor = false;
        for (const foeActive of pokemon.side.foes()) {
            if (["intimidate", "unnerve", "pressure"].includes(foeActive.ability)) abilityFactor = true;
        }
        if (abilityFactor) spawnMultiplier *= 1.2;
        if (pokemon.wasHitSuperEffective) spawnMultiplier *= 2.0;

        pokemon.battle.add("-sos", pokemon, pokemon.side.n + 1, callMultiplier, spawnMultiplier);
        pokemon.wasHitSuperEffective = false;
    }
}