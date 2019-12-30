package me.infinityz.practice;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import me.infinityz.UHC;

/**
 * PracticeManager
 */
public class PracticeManager {

    private UHC instance;
    public World practice_world;
    public boolean enabled;
    public int teleport_radius;
    public HashSet<UUID> practiceHashSet;

    public PracticeManager(UHC instance) {
        this.instance = instance;
        this.enabled = false;
        this.practice_world = instance.getServer()
                .createWorld(new WorldCreator("Practice").type(WorldType.FLAT).generateStructures(false));
        this.practice_world.setGameRuleValue("doMobSpawning", "false");
        this.practice_world.setGameRuleValue("doFireTick", "false");
        this.practice_world.setGameRuleValue("doDaylightCycle", "false");
        this.teleport_radius = 100;
        this.practiceHashSet = new HashSet<>();
    }

    public Location getLocation() {
        Location loc = new Location(practice_world, 0, 0, 0);
        loc.setX(loc.getX() + Math.random() * teleport_radius * 2.0 - teleport_radius);
        loc.setZ(loc.getZ() + Math.random() * teleport_radius * 2.0 - teleport_radius);
        return loc.getWorld().getHighestBlockAt(loc).getLocation().add(0.0, 2.0, 0.0);
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        // Check if the player is in the arena
        if (practiceHashSet.contains(player.getUniqueId())) {
            // Kill the player and remove from arena list.
            player.damage(player.getHealth());
            player.spigot().respawn();
            practiceHashSet.remove(player.getUniqueId());
        }
        ;
    }

}