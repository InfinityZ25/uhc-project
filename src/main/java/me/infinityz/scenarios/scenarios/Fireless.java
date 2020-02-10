package me.infinityz.scenarios.scenarios;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import me.infinityz.scenarios.IScenario;

/**
 * Fireless
 */
public class Fireless extends IScenario{
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Player
                && (event.getCause() == EntityDamageEvent.DamageCause.FIRE
                        || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                        || event.getCause() == EntityDamageEvent.DamageCause.LAVA))
            event.setCancelled(true);
    }

}