package com.timvisee.dungeonmaze.command.executable;

import com.timvisee.dungeonmaze.command.CommandParts;
import com.timvisee.dungeonmaze.command.ExecutableCommand;
import com.timvisee.dungeonmaze.loot.LootTableManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LootCommand extends ExecutableCommand {
    @Override
    public boolean executeCommand(CommandSender sender, CommandParts commandReference, CommandParts commandArguments) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        LootTableManager.getInstance().load();
        LootTableManager.getInstance().openGui((Player) sender);
        sender.sendMessage(ChatColor.GREEN + "Opening the Dungeon Maze loot table...");
        return true;
    }
}
