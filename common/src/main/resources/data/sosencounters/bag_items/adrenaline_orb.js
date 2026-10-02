{
    use(battle, pokemon, itemId, data) {
        battle.log = battle.log.filter(line => !(line.startsWith("|bagitem|") && line.includes("adrenaline_orb")));

        battle.adrenaline = true;
        battle.add("-adrenalineorb", data[0]);
    }
}
