package me.infinityz.scenarios.scenarios;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import me.infinityz.scenarios.IScenario;

/**
 * NoFall
 */
public class NoFall extends IScenario {

    @EventHandler
    public void onFall(EntityDamageEvent e) {
        if (e.getEntity() instanceof org.bukkit.entity.Player && e.getCause() == EntityDamageEvent.DamageCause.FALL)
            e.setCancelled(true);
    }

}