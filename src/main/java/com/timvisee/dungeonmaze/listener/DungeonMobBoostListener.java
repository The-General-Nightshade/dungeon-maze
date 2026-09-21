package com.timvisee.dungeonmaze.listener;

import com.timvisee.dungeonmaze.Core;
import com.timvisee.dungeonmaze.config.ConfigHandler;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class DungeonMobBoostListener implements Listener {

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        final ConfigHandler config = Core.getConfigHandler();
        if(config == null || !config.mobBoostEnabled)
            return;

        final Entity entity = event.getEntity();
        if(entity == null || !(entity instanceof LivingEntity))
            return;

        if(!event.getLocation().getWorld().getName().contains("dungeon") && !event.getLocation().getWorld().getName().contains("maze"))
            return;

        final LivingEntity living = (LivingEntity) entity;
        final double healthMultiplier = config.mobHealthMultiplier;
        final double damageMultiplier = config.mobDamageMultiplier;
        final double speedMultiplier = config.mobSpeedMultiplier;
        final double knockbackMultiplier = config.mobKnockbackMultiplier;

        if(living.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            double value = living.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * healthMultiplier;
            living.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(value);
            living.setHealth(Math.max(1.0, living.getHealth() * healthMultiplier));
        }

        if(living.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
            double value = living.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * damageMultiplier;
            living.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(value);
        }

        if(living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
            double value = living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * speedMultiplier;
            living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(value);
        }

        if(living.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE) != null) {
            double value = living.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getBaseValue() * knockbackMultiplier;
            living.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(value);
        }
    }
}
