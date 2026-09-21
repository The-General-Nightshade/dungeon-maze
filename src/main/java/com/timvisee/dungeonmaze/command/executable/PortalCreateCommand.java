package com.timvisee.dungeonmaze.command.executable;

import com.timvisee.dungeonmaze.command.CommandParts;
import com.timvisee.dungeonmaze.command.ExecutableCommand;
import com.timvisee.dungeonmaze.portal.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PortalCreateCommand extends ExecutableCommand {
    @Override
    public boolean executeCommand(CommandSender sender, CommandParts commandReference, CommandParts commandArguments) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if(commandArguments.getCount() < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /dm portal create <name> <dungeon_name> [target_world]");
            return true;
        }

        Player player = (Player) sender;
        String portalName = commandArguments.get(0);
        String dungeonName = commandArguments.get(1);
        String targetWorldName = commandArguments.getCount() >= 3 ? commandArguments.get(2) : null;

        if(targetWorldName == null || targetWorldName.trim().isEmpty()) {
            World target = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
            targetWorldName = target != null ? target.getName() : "world";
            if(player.getWorld().getName().equalsIgnoreCase(dungeonName)) {
                targetWorldName = Bukkit.getWorlds().isEmpty() ? "world" : Bukkit.getWorlds().get(0).getName();
            } else if(player.getWorld().getName().equalsIgnoreCase(targetWorldName)) {
                targetWorldName = dungeonName;
            }
        }

        PortalManager.getInstance().createPortal(portalName, player.getWorld().getName(), targetWorldName, player.getLocation());
        sender.sendMessage(ChatColor.GREEN + "Created portal '" + portalName + "' linking " + player.getWorld().getName() + " to " + targetWorldName + ".");
        return true;
    }
}
