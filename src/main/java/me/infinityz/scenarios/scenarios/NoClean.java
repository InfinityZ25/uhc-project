package me.infinityz.scenarios.scenarios;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.infinityz.UHC;
import me.infinityz.scenarios.IScenario;

/**
 * NoClean
 */
public class NoClean extends IScenario {
    public Map<UUID, Long> timeStamps;

    public NoClean() {
        this.description = "When you kill a player, you'll obtain 20s of invincibility.";
        this.timeStamps = new HashMap<>();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() == null)
            return;
        final Player killer = e.getEntity().getKiller();
        killer.sendMessage(ChatColor.RED + "[NoClean] You've recieved 20s of invincibility!");
        timeStamps.put(killer.getUniqueId(), System.currentTimeMillis() + 20000);
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            if (timeStamps.remove(killer.getUniqueId()) == null)
                return;
            killer.sendMessage(ChatColor.RED + "[NoClean] You've lost your invincibility!");

        }, 20 * 20L);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER)
            return;
        Long time = timeStamps.get(e.getEntity().getUniqueId());
        Entity damager = getDamager(e);
        if (time != null && time > System.currentTimeMillis()) {
            e.setCancelled(true);
            if (damager != null && damager instanceof Player) {
                Player pl = (Player) damager;
                pl.sendMessage(ChatColor.RED + "[NoClean] " + e.getEntity().getName() + " has invincibility for "
                        + (time / 1000.0D) + "s.");
            }
        }
        if (damager != null && damager instanceof Player) {
            Player damagerPlayer = (Player) damager;

            Long timeDamager = timeStamps.get(damagerPlayer.getUniqueId());

            if (timeDamager != null && timeDamager > System.currentTimeMillis()) {
                damagerPlayer.sendMessage(ChatColor.RED + "[NoClean] You've lost your invincibility");
                timeStamps.remove(damagerPlayer.getUniqueId());
            }
        }
    }

    Entity getDamager(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile) {
            return (Entity) ((Projectile) e.getDamager());
        }
        return e.getDamager();
    }

}