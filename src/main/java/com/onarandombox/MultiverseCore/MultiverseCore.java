package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MultiverseWorldManager;
import org.bukkit.plugin.PluginDescriptionFile;

public class MultiverseCore {
    private final PluginDescriptionFile description;

    public MultiverseCore() {
        this.description = new PluginDescriptionFile("Multiverse-Core", "2.5", "");
    }

    public PluginDescriptionFile getDescription() {
        return this.description;
    }

    public MultiverseWorldManager getMVWorldManager() {
        return new MultiverseWorldManager();
    }
}
