package com.onarandombox.MultiverseCore.api;

import org.bukkit.World;

public class MultiverseWorld {
    private final World world;

    public MultiverseWorld(World world) {
        this.world = world;
    }

    public String getGenerator() {
        return "";
    }

    public World getWorld() {
        return this.world;
    }
}
