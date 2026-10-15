package com.necro.sos.encounters.common.events;

import com.cobblemon.mod.common.api.reactive.CancelableObservable;
import com.cobblemon.mod.common.api.reactive.SimpleObservable;

public class SOSEvents {
    public static SimpleObservable<SOSBattleRollEvent> SOS_BATTLE_ROLL_CALL = new SimpleObservable<>();
    public static SimpleObservable<SOSBattleRollEvent> SOS_BATTLE_ROLL_SPAWN = new SimpleObservable<>();
    public static CancelableObservable<SOSBattleSpawnEvent.Pre> SOS_BATTLE_SPAWN_PRE = new CancelableObservable<>();
    public static SimpleObservable<SOSBattleSpawnEvent.Post> SOS_BATTLE_SPAWN_POST = new SimpleObservable<>();
}
