package com.necro.sos.encounters.common.showdown.instruction;

import com.bedrockk.molang.runtime.MoLangRuntime;
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.drop.DropTable;
import com.cobblemon.mod.common.api.moves.animations.ActionEffectContext;
import com.cobblemon.mod.common.api.moves.animations.ActionEffectTimeline;
import com.cobblemon.mod.common.api.moves.animations.ActionEffects;
import com.cobblemon.mod.common.api.moves.animations.UsersProvider;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.dispatch.ActionEffectInstruction;
import com.cobblemon.mod.common.battles.dispatch.DispatchResultKt;
import com.cobblemon.mod.common.battles.dispatch.UntilDispatch;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.battles.runner.ShowdownService;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.necro.asymmetric.battles.common.api.AsymmetricAPI;
import com.necro.asymmetric.battles.common.api.BattleParticipant;
import com.necro.asymmetric.battles.common.util.PokemonLocatorUtils;
import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.api.SOSManager;
import com.necro.sos.encounters.common.api.SOSResult;
import com.necro.sos.encounters.common.config.ConfigCache;
import com.necro.sos.encounters.common.util.ISOSCaller;
import kotlin.Pair;
import kotlin.Unit;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SOSInstruction implements ActionEffectInstruction {
    private CompletableFuture<?> future;
    private Set<String> holds;
    private final BattlePokemon pokemon;
    private final int side;
    private final float callMultiplier;
    private final float spawnChance;
    private SOSResult result;

    public SOSInstruction(PokemonBattle battle, BattleMessage message) {
        this.future = CompletableFuture.completedFuture(Unit.INSTANCE);
        this.holds = new HashSet<>();
        this.pokemon = message.battlePokemon(0, battle);
        String sideArg = message.argumentAt(1);
        this.side = sideArg != null ? Integer.parseInt(sideArg) : 4;
        String callMultiplierArg = message.argumentAt(2);
        this.callMultiplier = callMultiplierArg != null ? Float.parseFloat(callMultiplierArg) : 0.0f;
        String spawnChanceArg = message.argumentAt(3);
        this.spawnChance = spawnChanceArg != null ? Float.parseFloat(spawnChanceArg) : 0.0f;
        this.result = SOSResult.NONE;
    }

    @Override
    public @NotNull CompletableFuture<?> getFuture() {
        return this.future;
    }

    @Override
    public void setFuture(@NotNull CompletableFuture<?> future) {
        this.future = future;
    }

    @Override
    public @NotNull Set<String> getHolds() {
        return this.holds;
    }

    @Override
    public void setHolds(@NotNull Set<String> set) {
        this.holds = set;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(SOSEncounters.MODID, "sos");
    }

    @Override
    public void preActionEffect(@NotNull PokemonBattle battle) {
        if (this.pokemon == null || this.pokemon.getEntity() == null) return;
        PokemonEntity entity = this.pokemon.getEntity();
        SOSManager manager = ((ISOSCaller) entity).sos_getSOSManager();
        this.result = manager.rollCall(this.callMultiplier, this.spawnChance);
    }

    @Override
    public void runActionEffect(@NotNull PokemonBattle battle, @NotNull MoLangRuntime runtime) {
        if (this.result == SOSResult.NONE) return;
        battle.dispatch(() -> {
            if (this.pokemon == null || this.pokemon.getEntity() == null) return DispatchResultKt.getGO();

            battle.broadcastChatMessage(Component.translatable("sosencounters.battle.sos.call", this.pokemon.getName()));

            ActionEffectTimeline actionEffect = ActionEffects.INSTANCE.getActionEffects().get(ResourceLocation.fromNamespaceAndPath(SOSEncounters.MODID, "sos"));
            List<Object> providers = new ArrayList<>(List.of(battle));
            providers.add(new UsersProvider(this.pokemon.getEffectedPokemon().getEntity()));

            ActionEffectContext context = new ActionEffectContext(
                actionEffect, new HashSet<>(), providers, runtime, false, false,
                new ArrayList<>(), battle.getPlayers().getFirst().level()
            );

            if (this.pokemon.getEffectedPokemon().getEntity() != null) {
                this.setFuture(actionEffect.run(context));
                this.setHolds(context.getHolds());
                this.future.thenAccept(v -> this.holds.clear());
            }

            return new UntilDispatch(() -> !this.holds.contains("cry"));
        });
    }

    @Override
    public void postActionEffect(@NotNull PokemonBattle battle) {
        if (this.result == SOSResult.NONE) return;
        else if (this.pokemon == null || this.pokemon.getEntity() == null) return;

        battle.dispatch(() -> {
            battle.broadcastChatMessage(Component.literal("... ... ..."));
            return new UntilDispatch(() -> !this.holds.contains("wait"));
        });

        battle.dispatch(() -> {
            if (this.pokemon.getEntity() == null) return DispatchResultKt.getGO();
            else if (this.result == SOSResult.CALL) {
                battle.broadcastChatMessage(Component.translatable("sosencounters.battle.sos.failed", this.pokemon.getName()));
                return DispatchResultKt.getGO();
            }

            PokemonEntity entity = this.pokemon.getEntity();
            SOSManager manager = ((ISOSCaller) entity).sos_getSOSManager();
            ServerPlayer player;
            if (battle.getSide1().getActors()[0] instanceof PlayerBattleActor playerActor && playerActor.getEntity() != null && !playerActor.getActivePokemon().isEmpty()) player = playerActor.getEntity();
            else {
                battle.broadcastChatMessage(Component.translatable("sosencounters.battle.sos.failed", this.pokemon.getName()));
                return DispatchResultKt.getGO();
            }
            Pair<ServerLevel, Vec3> playerActivePos = playerActor.getActivePokemon().getFirst().getPosition();
            int otherSide = this.side == 2 ? 4 : 2;

            Pokemon pokemon = manager.rollSpawn(this.pokemon.getEffectedPokemon(), player);
            Vec3 spawnPos = this.getSendOutPosition(battle, pokemon, otherSide);
            if (spawnPos == null) spawnPos = entity.position();
            PokemonEntity newEntity = pokemon.sendOut((ServerLevel) entity.level(), spawnPos, null, p -> Unit.INSTANCE);
            if (newEntity == null) {
                battle.broadcastChatMessage(Component.translatable("sosencounters.battle.sos.failed", this.pokemon.getName()));
                return DispatchResultKt.getGO();
            }
            newEntity.lookAt(EntityAnchorArgument.Anchor.EYES, playerActivePos != null ? playerActivePos.getSecond() : player.position());
            ((ServerLevel) entity.level()).sendParticles(ParticleTypes.GUST_EMITTER_SMALL, spawnPos.x(), spawnPos.y(), spawnPos.z(), 1, 1.0, 0.0, 0.0, 0.0);
            newEntity.setDrops(new DropTable());
            ((ISOSCaller) newEntity).sos_setSOSManager(manager);
            ((ISOSCaller) newEntity).sos_setSOSSpawn();
            manager.setLastSpawn(newEntity);

            AsymmetricAPI.setMultiBattleActor(BattleParticipant.wild(newEntity).toActor(), battle, otherSide);
            newEntity.setBattleId(battle.getBattleId());
            if (ConfigCache.canSOS(newEntity)) {
                String[] messages = { String.format(">eval " +
                        "battle.sides[%1$d].pokemon.forEach(p => p.addVolatile('wild')); " +
                        "battle.calledSOS = true;",
                    otherSide - 1) };
                ShowdownService.Companion.getService().send(battle.getBattleId(), messages);
            }

            battle.broadcastChatMessage(Component.translatable("sosencounters.battle.sos.success", newEntity.getDisplayName()));
            return new UntilDispatch(() -> !this.holds.contains("effects"));
        });
    }

    private Vec3 getSendOutPosition(PokemonBattle battle, Pokemon pokemon, int side) {
        Vec3 wildPos = PokemonLocatorUtils.getAveragePosition(battle.getSide2().getActors());
        Vec3 playerPos = PokemonLocatorUtils.getAveragePosition(battle.getSide1().getActors());
        if (wildPos == null || playerPos == null) return null;

        Vec3 wildOffset = playerPos.subtract(wildPos);
        double wildDistance = wildOffset.length();
        if (wildDistance == 0.0) return null;

        float width = pokemon.getForm().getHitbox().width();
        float scale = pokemon.getForm().getBaseScale();
        float pokemonWidth = width * scale;

        Vec3 orthogonalVector = new Vec3(wildOffset.x, 0.0, wildOffset.z).normalize();
        if (orthogonalVector.lengthSqr() == 0.0) return null;
        orthogonalVector = orthogonalVector.cross(new Vec3(0.0, 1.0, 0.0));

        double sideOffset = ((side == 4 ? 1 : 0) - 0.5) * Math.max(pokemonWidth, 3.5);
        return wildPos.add(orthogonalVector.scale(sideOffset)).add(0.0, 1.0, 0.0);
    }
}
