package com.necro.sos.encounters.fabric.showdown;

import com.necro.sos.encounters.common.showdown.ShowdownLoader;
import net.fabricmc.loader.api.FabricLoader;

public class FabricShowdownLoader extends ShowdownLoader {
    @Override
    protected boolean isMegaShowdownLoaded() {
        return FabricLoader.getInstance().isModLoaded("mega_showdown");
    }

    @Override
    protected boolean isCobblemonRaidDensLoaded() {
        return FabricLoader.getInstance().isModLoaded("cobblemonraiddens");
    }
}
