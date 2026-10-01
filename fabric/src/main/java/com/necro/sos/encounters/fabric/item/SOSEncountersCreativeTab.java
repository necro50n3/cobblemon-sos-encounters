package com.necro.sos.encounters.fabric.item;

import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.item.SOSEncountersItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SOSEncountersCreativeTab {
    public static void registerCreativeTab() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(SOSEncounters.MODID, "sos_encounters_tab"),
            FabricItemGroup.builder().title(Component.translatable("itemgroup.sosencounters.sos_encounters_tab"))
                .icon(() -> new ItemStack(SOSEncountersItems.ADRENALINE_ORB))
                .displayItems((context, entries) -> {
                    entries.accept(SOSEncountersItems.ADRENALINE_ORB.value());
                }).build()
        );
    }
}
