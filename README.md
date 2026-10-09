[![Modrinth](https://img.shields.io/modrinth/dt/-------?style=for-the-badge&logo=modrinth&label=Modrinth)]()
[![CurseForge](https://img.shields.io/curseforge/dt/1723355?style=for-the-badge&logo=curseforge&label=Curseforge)](https://www.curseforge.com/minecraft/mc-mods/cobblemon-sos-encounters)
[![Discord](https://img.shields.io/discord/1355016729679499284?style=for-the-badge&logo=discord&label=Discord)](https://discord.gg/6jBar3y6nt)
[![GitHub](https://img.shields.io/badge/GitHub-565656?style=for-the-badge&logo=github)](https://github.com/necro50n3/cobblemon-sos-encounters)

# Cobblemon SOS Battles
![SOS Battles](https://i.imgur.com/wMSmlW7.gif)

## Features
Adds a faithful recreation of Gen 7's SOS Battles to Cobblemon!

Pokémon now have a chance to call on allies to defend themselves in battle!

The mod is completely configurable and features SOS chaining, Pokémon/Aspect blacklists and spawn overrides.

## Mechanics
- Remains faithful to the original mechanics from Pokémon Ultra Sun and Moon.
- Pokémon can only make a successful SOS call once per battle.
- Using an Adrenaline Orb will increase the chance of a successful SOS call and remove the SOS call limit.
- Pokémon that have a status condition cannot make an SOS call.

## Dependencies
- [Asymmetric Battles API](https://github.com/necro50n3/asymmetric-battles-api)

## Incompatibilities
- Genesis Forms

## Custom Spawning
### Basic Spawning (Config)
You can set up basic spawn settings with the `spawn_overrides` config value.
This allows for customising call rates, level ranges and Pokémon spawns.

The only required field is `properties`.
```
-   properties: charizard alpha
    call_rate: 0.03
    spawn_weights:
        charmander: 10.0
    level_offset:
        min: -5
        max: 0
```

### Advanced Spawning (Datapack)
Advanced spawn settings allow for additional conditional spawning, conditional weights, and level ranges or level range offsets per-spawn.
Advanced spawning entries must be in the `data/<namespace>/battle_spawns/sos` directory.
Supports `conditions`, `anticonditions`, and `weightMultipliers`. Additionally supports registration conditions `neededInstalledMods` and `neededUninstalledMods`.
Refer to Cobblemon's [Spawn Detail Presets](https://wiki.cobblemon.com/index.php/Spawn_Detail_Presets).


The only required field is `pokemon`.
```
{
    "pokemon": "wooper paldean",
    "callRate": 0.03,
    "spawns": [
        {
            "pokemon": "wooper paldean",
            "weight": 10.0,
            "level": "20-25"
        },
        {
            "pokemon": "clodsire",
            "levelOffset": "-5-10",
            "conditions": {
                "isRaining": true
            }
        }
    ]
}
```