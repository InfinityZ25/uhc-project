package me.infinityz.practice;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

    // Extend as a Listener for events, make sure to give the right priority to
    // avoid other events interception.
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
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        // Check if the player is in the arena
        if (practiceHashSet.contains(player.getUniqueId())) {
            // Kill the player and remove from arena list.
            player.damage(player.getHealth());
            player.spigot().respawn();
            practiceHashSet.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // Handle the health change, the anti respawn screen, try to play the animation
        // without actually allowing death.
        if (!isInPractice(e.getEntity().getUniqueId()))
            return;
        final Player player = e.getEntity();
        // Set health back to 20 and rescatter, maybe send the death message.
        player.setHealth(20.0D);
        givePracticeKit(player);
        player.teleport(getLocation());
        player.sendMessage("You were killed");
        // Ensure the killer is a player and not anyother kindof entity
        if (player.getKiller() == null || !(player.getKiller() instanceof Player))
            return;
        final Player killer = player.getKiller();
        // Now that we know who the killer is, reward them.
        if (isInPractice(killer.getUniqueId())) {
            // TODO: MAKE THE REWARD SYSTEM, GIVE SOMEETHING TO KILLER
            killer.sendMessage("You've killed " + player.getCustomName() + "!");
            return;
        }
        killer.sendMessage(
                "A player that you damaged has died and you technically killed him. Congratulations I guess.");

    }

    public void joinPractice(final Player p) {
        // Ensure the player is not already in the hashset
        if (practiceHashSet.contains(p.getUniqueId())) {
            p.sendMessage("You are already in practice!");
            return;
        }
        assert p.isOnline();
        p.teleport(getLocation());
        givePracticeKit(p);

        practiceHashSet.add(p.getUniqueId());
    }

    public void joinPractice(final UUID uuid) {
        // Recursive method for the uuid.
        joinPractice(Bukkit.getPlayer(uuid));
    }

    public void leavePractice(final Player player) {
        if (!isInPractice(player.getUniqueId()))
            return;
        practiceHashSet.remove(player.getUniqueId());
        player.damage(player.getHealth());
        player.setHealth(20.0D);
        wipePlayer(player);
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0.0, 2.0, 0.0));
    }

    boolean isInPractice(UUID uuid) {
        return practiceHashSet.contains(uuid);
    }

    void givePracticeKit(final Player player) {
        wipePlayer(player);
        final PlayerInventory inventory = player.getInventory();
        inventory.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        inventory.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        inventory.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        inventory.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        inventory.setHeldItemSlot(0);
        inventory.setItem(0, new ItemStack(Material.DIAMOND_SWORD));
        inventory.setItem(1, new ItemStack(Material.FISHING_ROD));
        inventory.setItem(2, new ItemStack(Material.BOW));

        inventory.setItem(4, new ItemStack(Material.WATER_BUCKET));
        inventory.setItem(5, new ItemStack(Material.COOKED_BEEF, 32));
        inventory.setItem(9, new ItemStack(Material.ARROW, 16));
    }

    void wipePlayer(final Player player) {
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setHealth(20.0D);
        player.setSaturation(20.0F);        
        player.getActivePotionEffects().forEach(effect -> {
            player.removePotionEffect(effect.getType());
        });
    }

}