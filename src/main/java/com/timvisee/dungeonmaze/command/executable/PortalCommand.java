package com.timvisee.dungeonmaze.command.executable;

import com.timvisee.dungeonmaze.command.CommandParts;
import com.timvisee.dungeonmaze.command.ExecutableCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PortalCommand extends ExecutableCommand {
    @Override
    public boolean executeCommand(CommandSender sender, CommandParts commandReference, CommandParts commandArguments) {
        sender.sendMessage(ChatColor.YELLOW + "Portal usage:");
        sender.sendMessage(ChatColor.GOLD + "/dm portal create <name> <dungeon_name> [overworld_name]");
        sender.sendMessage(ChatColor.GOLD + "/dm portal remove <name>");
        sender.sendMessage(ChatColor.GOLD + "/dm wand");
        return true;
    }
}
