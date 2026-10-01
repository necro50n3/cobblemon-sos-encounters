package com.necro.sos.encounters.neoforge.item;

import com.necro.sos.encounters.common.SOSEncounters;
import com.necro.sos.encounters.common.item.AdrenalineOrbItem;
import com.necro.sos.encounters.common.item.SOSEncountersItems;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SOSEncountersItemsNeoForge {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SOSEncounters.MODID);

    public static void registerItems(IEventBus modBus) {
        SOSEncountersItems.ADRENALINE_ORB = registerItem("adrenaline_orb", AdrenalineOrbItem::new);

        ITEMS.register(modBus);
    }

    private static Holder<Item> registerItem(String name, Supplier<Item> item) {
        return ITEMS.register(name, item);
    }
}
