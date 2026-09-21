package com.timvisee.dungeonmaze.portal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PortalManager {
    private static final PortalManager INSTANCE = new PortalManager();
    private static final String FILE_NAME = "portals.yml";
    private final File file = new File("./plugins/DungeonMaze/" + FILE_NAME);
    private final Map<String, PortalRecord> portals = new LinkedHashMap<>();

    public static PortalManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        this.portals.clear();
        File dir = file.getParentFile();
        if(dir != null && !dir.exists() && !dir.mkdirs()) {
            return;
        }

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(IOException ignored) {
            }
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if(config.isConfigurationSection("portals")) {
            for(String key : config.getConfigurationSection("portals").getKeys(false)) {
                String name = key;
                String worldName = config.getString("portals." + key + ".world", "world");
                String targetWorldName = config.getString("portals." + key + ".targetWorld", "world");
                double x = config.getDouble("portals." + key + ".x", 0.0);
                double y = config.getDouble("portals." + key + ".y", 64.0);
                double z = config.getDouble("portals." + key + ".z", 0.0);
                float yaw = (float) config.getDouble("portals." + key + ".yaw", 0.0);
                float pitch = (float) config.getDouble("portals." + key + ".pitch", 0.0);
                this.portals.put(name, new PortalRecord(name, worldName, targetWorldName, x, y, z, yaw, pitch));
            }
        }
    }

    public void save() {
        File dir = file.getParentFile();
        if(dir != null && !dir.exists() && !dir.mkdirs()) {
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        int index = 0;
        for(Map.Entry<String, PortalRecord> entry : this.portals.entrySet()) {
            PortalRecord record = entry.getValue();
            config.set("portals." + index + ".name", record.name);
            config.set("portals." + index + ".world", record.worldName);
            config.set("portals." + index + ".targetWorld", record.targetWorldName);
            config.set("portals." + index + ".x", record.location.getX());
            config.set("portals." + index + ".y", record.location.getY());
            config.set("portals." + index + ".z", record.location.getZ());
            config.set("portals." + index + ".yaw", record.location.getYaw());
            config.set("portals." + index + ".pitch", record.location.getPitch());
            index++;
        }

        try {
            config.save(file);
        } catch(IOException ignored) {
        }
    }

    public void createPortal(String name, String worldName, String targetWorldName, Location location) {
        if(name == null || name.trim().isEmpty())
            return;
        if(location == null)
            return;

        this.portals.put(name, new PortalRecord(name, worldName, targetWorldName, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));
        createPortalBlock(location);
        save();
    }

    public boolean removePortal(String name) {
        if(name == null || !this.portals.containsKey(name))
            return false;

        this.portals.remove(name);
        save();
        return true;
    }

    public void teleportPlayer(Player player, String portalName) {
        PortalRecord record = this.portals.get(portalName);
        if(record == null || player == null)
            return;

        World world = Bukkit.getWorld(record.targetWorldName);
        if(world == null)
            return;

        player.teleport(new Location(world, record.location.getX(), record.location.getY(), record.location.getZ(), record.location.getYaw(), record.location.getPitch()));
    }

    public List<String> getPortalNames() {
        return new ArrayList<>(this.portals.keySet());
    }

    public PortalRecord getPortal(String name) {
        return this.portals.get(name);
    }

    public boolean hasPortal(String name) {
        return this.portals.containsKey(name);
    }

    private void createPortalBlock(Location location) {
        if(location == null || location.getWorld() == null)
            return;

        Block block = location.getBlock();
        if(block.getType() == Material.AIR) {
            block.setType(Material.END_PORTAL_FRAME);
        }
    }

    public static class PortalRecord {
        public final String name;
        public final String worldName;
        public final String targetWorldName;
        public final Location location;

        public PortalRecord(String name, String worldName, String targetWorldName, double x, double y, double z, float yaw, float pitch) {
            this.name = name;
            this.worldName = worldName;
            this.targetWorldName = targetWorldName;
            this.location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        }
    }
}
