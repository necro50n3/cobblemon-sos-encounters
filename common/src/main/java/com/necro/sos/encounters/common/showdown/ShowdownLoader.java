package com.necro.sos.encounters.common.showdown;

import com.necro.sos.encounters.common.SOSEncounters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Implementation of Mega Showdown's Showdown Loader when MSD is not installed.
public abstract class ShowdownLoader {
    protected abstract boolean isMegaShowdownLoaded();
    protected abstract boolean isCobblemonRaidDensLoaded();

    public void load() {
        if (this.isMegaShowdownLoaded() || this.isCobblemonRaidDensLoaded()) return;
        SOSEncounters.LOGGER.info("Initiating showdown files");

        Path showdown_sim = Path.of("./showdown/sim");
        Path showdown_data = Path.of("./showdown/data");
        Path showdown = Path.of("./showdown");
        Path showdown_mod_data = Path.of("./showdown/data/mods/cobblemon");

        try {
            Files.createDirectories(showdown_sim);
            Files.createDirectories(showdown_data);

            yoink("assets/sosencounters/showdown/conditions.js", showdown_data.resolve("conditions.js"));
            yoink("assets/sosencounters/showdown/index.js", showdown.resolve("index.js"));

            if (!Files.exists(showdown_mod_data.resolve("conditions.js"))) {
                yoink("assets/sosencounters/showdown/mods/conditions.js", showdown_mod_data.resolve("conditions.js"));
            }
        } catch (IOException e) {
            SOSEncounters.LOGGER.error("Failed to load showdown files: {}", e.getMessage());
        }
    }

    private void yoink(String resourcePath, Path targetPath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) return;
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
