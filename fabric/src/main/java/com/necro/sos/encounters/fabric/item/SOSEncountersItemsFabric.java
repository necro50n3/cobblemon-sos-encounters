package com.necro.sos.encounters.fabric.item;

import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.item.AdrenalineOrbItem;
import com.necro.sos.encounters.common.item.SOSEncountersItems;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class SOSEncountersItemsFabric {
    public static void registerItems() {
        SOSEncountersItems.ADRENALINE_ORB = registerItem("adrenaline_orb", new AdrenalineOrbItem());
    }

    private static Holder<Item> registerItem(String name, Item item) {
        return Registry.registerForHolder(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(SOSEncounters.MODID, name),
            item
        );
    }
}
