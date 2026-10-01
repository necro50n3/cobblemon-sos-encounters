package com.necro.sos.encounters.neoforge.item;

import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.item.SOSEncountersItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SOSEncountersCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SOSEncounters.MODID);

    public static void registerCreativeTab(IEventBus modBus) {
        CREATIVE_TABS.register("sos_encounters_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("itemgroup.sosencounters.sos_encounters_tab"))
                .icon(() -> new ItemStack(SOSEncountersItems.ADRENALINE_ORB))
                .displayItems((context, entries) -> {
                    entries.accept(SOSEncountersItems.ADRENALINE_ORB.value());
                }).build());

        CREATIVE_TABS.register(modBus);
    }
}
