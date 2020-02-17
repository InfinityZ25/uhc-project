package me.infinityz.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import me.infinityz.UHC;

/**
 * ScatterListeners
 */
public class ScatterListeners extends SkeletonListener {

    public ScatterListeners(UHC instance) {
        super(instance);
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(PlayerInteractAtEntityEvent e) {
        e.setCancelled(true);
    }

}