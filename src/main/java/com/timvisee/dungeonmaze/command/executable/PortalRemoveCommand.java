package com.timvisee.dungeonmaze.command.executable;

import com.timvisee.dungeonmaze.command.CommandParts;
import com.timvisee.dungeonmaze.command.ExecutableCommand;
import com.timvisee.dungeonmaze.portal.PortalManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PortalRemoveCommand extends ExecutableCommand {
    @Override
    public boolean executeCommand(CommandSender sender, CommandParts commandReference, CommandParts commandArguments) {
        if(commandArguments.getCount() < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /dm portal remove <name>");
            return true;
        }

        String name = commandArguments.get(0);
        if(PortalManager.getInstance().removePortal(name)) {
            sender.sendMessage(ChatColor.GREEN + "Removed portal '" + name + "'.");
        } else {
            sender.sendMessage(ChatColor.RED + "No portal named '" + name + "' was found.");
        }
        return true;
    }
}
