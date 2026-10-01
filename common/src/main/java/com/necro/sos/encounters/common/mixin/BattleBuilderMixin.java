package com.necro.sos.encounters.common.mixin;

import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.BattleStartResult;
import com.cobblemon.mod.common.battles.runner.ShowdownService;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.necro.asymmetric.battles.common.api.AsymmetricBattleBuilder;
import com.necro.asymmetric.battles.common.api.BattleParticipant;
import com.necro.sos.encounters.common.config.ConfigCache;
import com.necro.sos.encounters.common.util.ISOSCaller;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = BattleBuilder.class)
public class BattleBuilderMixin {
    @Inject(method = "pve(Lnet/minecraft/server/level/ServerPlayer;Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;Ljava/util/UUID;Lcom/cobblemon/mod/common/battles/BattleFormat;ZZFLcom/cobblemon/mod/common/api/storage/party/PartyStore;)Lcom/cobblemon/mod/common/battles/BattleStartResult;", at = @At("HEAD"), remap = false, cancellable = true)
    private void setBattleStartCondition(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon, BattleFormat battleFormat, boolean cloneParties, boolean healFirst, float fleeDistance, PartyStore party, CallbackInfoReturnable<BattleStartResult> cir) {
        if (!ConfigCache.canSOS(pokemonEntity) || !battleFormat.getBattleType().getName().equals("singles")) return;
        cir.setReturnValue(AsymmetricBattleBuilder.multiBattle(BattleParticipant.player(player, leadingPokemon), BattleParticipant.wild(pokemonEntity))
            .ifSuccessful(battle -> {
                ((ISOSCaller) pokemonEntity).sos_initSOSManager();
                String[] message = { ">eval " +
                    "battle.sides[1].pokemon.forEach(p => p.addVolatile('wild')); " +
                    "battle.sides[3].pokemon.forEach(p => p.addVolatile('wild'));"
                };
                ShowdownService.Companion.getService().send(battle.getBattleId(), message);
                return Unit.INSTANCE;
            })
        );
    }
}
