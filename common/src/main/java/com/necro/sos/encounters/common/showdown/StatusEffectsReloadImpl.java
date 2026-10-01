package com.necro.sos.encounters.common.showdown;

import com.necro.sos.encounters.common.SOSEncounters;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StatusEffectsReloadImpl {
    protected final Map<String, String> statuses;

    public StatusEffectsReloadImpl() {
        this.statuses = new HashMap<>();
    }

    public void load(@NotNull ResourceManager manager) {
        this.statuses.clear();

        manager.listResources("sosencounters/showdown/conditions", path -> path.toString().endsWith(".js")).forEach((id, resource) -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                String status = reader.lines().collect(Collectors.joining("\n"));
                String statusId = id.getPath().replace("sosencounters/showdown/conditions/", "").replace(".js", "");
                this.statuses.put(statusId, status);
            } catch (Exception e) {
                SOSEncounters.LOGGER.error("Failed to load status effect {}", id, e);
            }
        });

        this.postLoad();
    }

    protected void postLoad() {
        throw new NotImplementedException();
    }
}
