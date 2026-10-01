package com.necro.sos.encounters.neoforge.showdown;

import com.necro.sos.encounters.common.showdown.ShowdownLoader;
import net.neoforged.fml.loading.LoadingModList;

public class NeoForgeShowdownLoader extends ShowdownLoader {
    @Override
    protected boolean isMegaShowdownLoaded() {
        return LoadingModList.get().getModFileById("mega_showdown") != null;
    }

    @Override
    protected boolean isCobblemonRaidDensLoaded() {
        return LoadingModList.get().getModFileById("cobblemonraiddens") != null;
    }
}
