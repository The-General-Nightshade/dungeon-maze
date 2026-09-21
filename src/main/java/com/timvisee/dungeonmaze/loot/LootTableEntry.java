package com.timvisee.dungeonmaze.loot;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class LootTableEntry {
    private final ItemStack item;
    private final int weight;

    public LootTableEntry(ItemStack item, int weight) {
        this.item = item != null ? item.clone() : new ItemStack(org.bukkit.Material.AIR);
        this.weight = Math.max(1, weight);
    }

    public static LootTableEntry fromSection(ConfigurationSection section) {
        if(section == null)
            return null;

        final Object rawItem = section.get("item");
        if(!(rawItem instanceof Map))
            return null;

        ItemStack item;
        try {
            item = ItemStack.deserialize((Map<String, Object>) rawItem);
        } catch(ClassCastException e) {
            return null;
        }

        return new LootTableEntry(item, section.getInt("weight", 1));
    }

    public ItemStack getItem() {
        return this.item.clone();
    }

    public int getWeight() {
        return this.weight;
    }
}
