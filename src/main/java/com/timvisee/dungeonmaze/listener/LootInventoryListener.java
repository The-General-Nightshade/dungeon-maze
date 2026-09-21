package com.timvisee.dungeonmaze.listener;

import com.timvisee.dungeonmaze.loot.LootTableEntry;
import com.timvisee.dungeonmaze.loot.LootTableManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;

        Inventory inventory = event.getInventory();
        if(inventory == null || inventory.getHolder() == null)
            return;

        if(!(inventory.getHolder() instanceof LootTableManager.LootInventoryHolder))
            return;

        if(event.getRawSlot() < 0 || event.getRawSlot() >= inventory.getSize())
            return;

        final int addSlot = Math.max(0, inventory.getSize() - 1);
        if(event.getRawSlot() == addSlot) {
            event.setCancelled(true);
            ItemStack item = event.getCursor();
            if(item == null || item.getType().isAir())
                return;
            LootTableManager.getInstance().addItem(item.clone());
            event.getWhoClicked().sendMessage(ChatColor.GREEN + "Added item to the loot table.");
            event.getCursor().setAmount(0);
            return;
        }

        if(event.getCurrentItem() != null && event.getCurrentItem().getType() != org.bukkit.Material.AIR) {
            event.setCancelled(true);
            LootTableManager.getInstance().removeItem(event.getCurrentItem());
            event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Removed item from the loot table.");
            event.getInventory().setItem(event.getSlot(), null);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if(inventory == null || inventory.getHolder() == null)
            return;

        if(!(inventory.getHolder() instanceof LootTableManager.LootInventoryHolder))
            return;

        List<ItemStack> items = new ArrayList<>();
        for(ItemStack stack : inventory.getContents()) {
            if(stack != null && stack.getType() != org.bukkit.Material.AIR)
                items.add(stack.clone());
        }
        LootTableManager.getInstance().replaceEntries(items);
        LootTableManager.getInstance().save();
    }
}
