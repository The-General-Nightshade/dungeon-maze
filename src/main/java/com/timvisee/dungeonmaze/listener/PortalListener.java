package com.timvisee.dungeonmaze.listener;

import com.timvisee.dungeonmaze.portal.PortalManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PortalListener implements Listener {

    private Material getPortalFrameMaterial() {
        Material material = Material.getMaterial("ENDER_PORTAL_FRAME");
        if(material != null)
            return material;
        return Material.getMaterial("END_PORTAL_FRAME");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getTo() == null)
            return;

        Player player = event.getPlayer();
        World world = player.getWorld();
        Block block = world.getBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
        if(block.getType() != getPortalFrameMaterial())
            return;

        for(String portalName : PortalManager.getInstance().getPortalNames()) {
            PortalManager.PortalRecord record = PortalManager.getInstance().getPortal(portalName);
            if(record == null || !record.worldName.equalsIgnoreCase(world.getName()))
                continue;

            Location l = record.location;
            if(l == null || l.getWorld() == null)
                continue;

            if(Math.abs(l.getX() - player.getLocation().getX()) < 2.0 && Math.abs(l.getZ() - player.getLocation().getZ()) < 2.0) {
                World targetWorld = l.getWorld();
                if(targetWorld == null)
                    continue;
                player.teleport(new Location(targetWorld, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch()));
                return;
            }
        }
    }
}
