package com.timvisee.dungeonmaze.command.executable;

import com.timvisee.dungeonmaze.command.CommandParts;
import com.timvisee.dungeonmaze.command.ExecutableCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class WandCommand extends ExecutableCommand {
    @Override
    public boolean executeCommand(CommandSender sender, CommandParts commandReference, CommandParts commandArguments) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta meta = wand.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Dungeon Portal Wand");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Use this wand to create or remove",
                    ChatColor.GRAY + "a dungeon portal.",
                    ChatColor.YELLOW + "Use: /dm portal create <name> <dungeon_name>"
            ));
            meta.setUnbreakable(true);
            wand.setItemMeta(meta);
        }

        player.getInventory().addItem(wand);
        player.sendMessage(ChatColor.GREEN + "You received the Dungeon Portal Wand.");
        return true;
    }
}
