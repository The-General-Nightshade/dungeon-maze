package com.onarandombox.MultiverseCore.api;

import org.bukkit.World;

public class MultiverseWorldManager {
    public MultiverseWorld getMVWorld(World world) {
        return new MultiverseWorld(world);
    }
}
