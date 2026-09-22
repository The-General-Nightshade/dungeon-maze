package com.timvisee.dungeonmaze.loot;

import com.timvisee.dungeonmaze.Core;
import com.timvisee.dungeonmaze.DungeonMaze;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LootTableManager {
    private static final LootTableManager INSTANCE = new LootTableManager();
    private static final String FILE_NAME = "loot.yml";
    private final File file = new File("./plugins/DungeonMaze/" + FILE_NAME);
    private final List<LootTableEntry> entries = new ArrayList<>();

    public static LootTableManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        this.entries.clear();

        if(!file.exists()) {
            ensureDefaultFile();
        }

        final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if(config.isConfigurationSection("loot")) {
            for(String key : config.getConfigurationSection("loot").getKeys(false)) {
                final Object entryValue = config.get("loot." + key);
                if(!(entryValue instanceof Map))
                    continue;

                final Map<String, Object> map = (Map<String, Object>) entryValue;
                if(!map.containsKey("item") || !map.containsKey("weight"))
                    continue;

                final ItemStack item = ItemStack.deserialize((Map<String, Object>) map.get("item"));
                final int weight = Math.max(1, ((Number) map.get("weight")).intValue());
                this.entries.add(new LootTableEntry(item, weight));
            }
        }

        if(this.entries.isEmpty()) {
            this.entries.addAll(createFallbackLoot());
            save();
        }
    }

    private void ensureDefaultFile() {
        final File dir = file.getParentFile();
        if(dir != null && !dir.exists() && !dir.mkdirs()) {
            return;
        }

        if(DungeonMaze.instance != null && DungeonMaze.instance.getResource(FILE_NAME) != null) {
            DungeonMaze.instance.saveResource(FILE_NAME, false);
        }

        if(!file.exists()) {
            try {
                file.createNewFile();
                final FileConfiguration empty = new YamlConfiguration();
                empty.set("loot", new ArrayList<>());
                empty.save(file);
            } catch(IOException ignored) {
            }
        }
    }

    public void save() {
        final FileConfiguration config = new YamlConfiguration();
        final List<Map<String, Object>> serializableList = new ArrayList<>();

        for(LootTableEntry entry : this.entries) {
            final Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("item", entry.getItem().serialize());
            map.put("weight", entry.getWeight());
            serializableList.add(map);
        }

        config.set("loot", serializableList);
        try {
            config.save(file);
        } catch(IOException e) {
            Core.getLogger().warning("Failed to save loot table: " + e.getMessage());
        }
    }

    public List<LootTableEntry> getEntries() {
        return Collections.unmodifiableList(this.entries);
    }

    public void addItem(ItemStack item) {
        if(item == null || item.getType() == org.bukkit.Material.AIR)
            return;
        this.entries.add(new LootTableEntry(item, 1));
        save();
    }

    public void removeItem(ItemStack item) {
        if(item == null || item.getType() == org.bukkit.Material.AIR)
            return;

        for(int i = 0; i < this.entries.size(); i++) {
            final ItemStack entryItem = this.entries.get(i).getItem();
            if(entryItem != null && entryItem.isSimilar(item)) {
                this.entries.remove(i);
                save();
                return;
            }
        }
    }

    public void replaceEntries(List<ItemStack> items) {
        this.entries.clear();
        for(ItemStack item : items) {
            if(item == null || item.getType() == org.bukkit.Material.AIR)
                continue;
            this.entries.add(new LootTableEntry(item, 1));
        }
        if(this.entries.isEmpty()) {
            this.entries.addAll(createFallbackLoot());
        }
        save();
    }

    public List<ItemStack> generateChestContents(Random random, int minItems, int maxItems) {
        if(this.entries.isEmpty())
            return new ArrayList<>();

        final int count = Math.max(minItems, Math.min(maxItems, minItems + (maxItems > minItems && random.nextBoolean() ? random.nextInt(maxItems - minItems + 1) : 0)));
        final List<ItemStack> result = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            final LootTableEntry entry = pickWeighted(random);
            if(entry != null)
                result.add(entry.getItem());
        }
        return result;
    }

    public LootTableEntry pickWeighted(Random random) {
        if(this.entries.isEmpty())
            return null;

        int totalWeight = 0;
        for(LootTableEntry entry : this.entries)
            totalWeight += entry.getWeight();

        if(totalWeight <= 0)
            return null;

        int roll = random.nextInt(totalWeight);
        int current = 0;
        for(LootTableEntry entry : this.entries) {
            current += entry.getWeight();
            if(roll < current)
                return entry;
        }

        return this.entries.get(this.entries.size() - 1);
    }

    public void openGui(Player player) {
        final int slotCount = Math.max(27, ((this.entries.size() + 8) / 9) * 9 + 9);
        final Inventory inventory = Bukkit.createInventory(new LootInventoryHolder(), slotCount, ChatColor.DARK_AQUA + "Dungeon Loot");

        for(LootTableEntry entry : this.entries) {
            inventory.addItem(entry.getItem());
        }

        final ItemStack addItem = new ItemStack(org.bukkit.Material.EMERALD_BLOCK);
        final ItemMeta meta = addItem.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Add item to loot table");
            meta.setLore(java.util.Collections.singletonList(ChatColor.GRAY + "Drop or place an item here, then close the GUI."));
            addItem.setItemMeta(meta);
        }
        inventory.setItem(Math.max(0, slotCount - 1), addItem);

        player.openInventory(inventory);
    }

    public static class LootInventoryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private List<LootTableEntry> createFallbackLoot() {
        final List<LootTableEntry> fallback = new ArrayList<>();
        fallback.add(new LootTableEntry(new ItemStack(org.bukkit.Material.DIAMOND), 10));
        fallback.add(new LootTableEntry(new ItemStack(org.bukkit.Material.GOLDEN_APPLE), 5));
        fallback.add(new LootTableEntry(new ItemStack(org.bukkit.Material.IRON_SWORD), 12));
        fallback.add(new LootTableEntry(new ItemStack(org.bukkit.Material.BOW), 10));
        fallback.add(new LootTableEntry(new ItemStack(org.bukkit.Material.COOKED_BEEF, 4), 20));
        fallback.add(new LootTableEntry(new ItemStack(org.bukkit.Material.ARROW, 16), 20));
        fallback.add(new LootTableEntry(new ItemStack(org.bukkit.Material.EMERALD, 8), 18));
        return fallback;
    }
}
