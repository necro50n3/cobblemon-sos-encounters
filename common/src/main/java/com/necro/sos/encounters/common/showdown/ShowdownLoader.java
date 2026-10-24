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
    protected abstract boolean isGenesisFormsLoaded();
    protected abstract boolean isCobblemonRaidDensLoaded();

    public void load() {
        if (this.isMegaShowdownLoaded() || this.isCobblemonRaidDensLoaded()) return;
        SOSEncounters.LOGGER.info("Initiating showdown files");

        Path showdown = Path.of("./showdown");

        try {
            String path = this.isGenesisFormsLoaded() ? "assets/sosencounters/showdown/genesisforms/index.js" : "assets/sosencounters/showdown/index.js";
            yoink(path, showdown.resolve("index.js"));
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
