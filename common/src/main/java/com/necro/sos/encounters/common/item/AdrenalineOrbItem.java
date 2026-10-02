package com.necro.sos.encounters.common.item;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.BagItemActionResponse;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.necro.sos.encounters.common.util.IAdrenalineHolder;
import com.necro.sos.encounters.common.util.SOSEncountersUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.cobblemon.mod.common.util.LocalizationUtilsKt.battleLang;

public class AdrenalineOrbItem extends Item {
    private final BagItem bagItem;

    public AdrenalineOrbItem() {
        super(new Item.Properties());
        this.bagItem = new BagItem() {
            @Override
            public @NotNull String getShowdownInput(@NotNull BattleActor actor, @NotNull BattlePokemon pokemon, @Nullable String data) {
                return "adrenaline_orb";
            }

            @Override
            public boolean canUse(@NotNull ItemStack itemStack, @NotNull PokemonBattle battle, @NotNull BattlePokemon target) {
                return SOSEncountersUtils.isSOSBattle(battle) && !((IAdrenalineHolder) battle).sos_hasUsedAdrenaline() && target.getActor().getType() == ActorType.WILD && target.getHealth() > 0;
            }

            @Override
            public @NotNull Item getReturnItem() {
                return Items.AIR;
            }

            @Override
            public @NotNull String getItemName() {
                return "item.sosencounters.adrenaline_orb";
            }
        };
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResultHolder.success(user.getItemInHand(hand));
        ServerPlayer player = (ServerPlayer) user;
        ItemStack itemStack = user.getItemInHand(hand);
        PokemonBattle battle = BattleRegistry.getBattleByParticipatingPlayer(player);
        if (battle == null) return InteractionResultHolder.consume(itemStack);
        else if (!SOSEncountersUtils.isSOSBattle(battle)) {
            player.sendSystemMessage(Component.translatable("sosencounters.battle.adrenaline_orb.not_wild_battle").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.consume(itemStack);
        }
        else if (((IAdrenalineHolder) battle).sos_hasUsedAdrenaline()) {
            player.sendSystemMessage(Component.translatable("sosencounters.battle.adrenaline_orb.already_used").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.consume(itemStack);
        }

        BattleActor actor = battle.getActor(player);
        if (actor == null) return InteractionResultHolder.consume(itemStack);
        if (!actor.canFitForcedAction()) {
            player.sendSystemMessage(battleLang("bagitem.cannot").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.consume(itemStack);
        }

        List<ActiveBattlePokemon> allWild = battle.getSide2().getActivePokemon();
        Optional<ActiveBattlePokemon> activeWild = allWild.stream().filter(p -> p.getBattlePokemon() != null).findFirst();
        if (activeWild.isEmpty()) return InteractionResultHolder.consume(itemStack);

        player.playSound(CobblemonSounds.ITEM_USE, 1F, 1F);
        actor.forceChoose(new BagItemActionResponse(this.bagItem, activeWild.get().getBattlePokemon(), null));
        ((IAdrenalineHolder) battle).sos_setAdrenalineUsed(true);
        itemStack.consume(1, player);
        CriteriaTriggers.CONSUME_ITEM.trigger(player, itemStack);
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @NotNull TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.sosencounters.adrenaline_orb.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
