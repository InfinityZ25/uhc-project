package me.infinityz.scenarios.scenarios;

import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import me.infinityz.scenarios.IScenario;

/**
 * Fireless
 */
public class Fireless extends IScenario {

    public Fireless() {
        this.description = "Fire damaged is disabled in the overworld.\nNether fire damage is enabled!";

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && (event.getCause() == EntityDamageEvent.DamageCause.FIRE
                || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || event.getCause() == EntityDamageEvent.DamageCause.LAVA)) {
            if (event.getEntity().getWorld().getEnvironment() == Environment.NETHER)
                return;
            event.setCancelled(true);
        }
    }

}