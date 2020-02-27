package me.infinityz.practice;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.infinityz.UHC;

/**
 * PracticeListener
 */
public class PracticeListener implements Listener {
    PracticeManager practiceManager;

    public PracticeListener(PracticeManager practiceManager) {
        this.practiceManager = practiceManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        e.setQuitMessage("");
        // Check if the player is in the arena
        if (practiceManager.practiceHashSet.contains(player.getUniqueId())) {
            // Kill the player and remove from arena list.
            player.damage(player.getHealth());
            player.spigot().respawn();
            practiceManager.practiceHashSet.remove(player.getUniqueId());
        }
    }
    /*
     * 
     * @EventHandler public void onss(BlockBreakEvent e) { e.setCancelled(true);
     * 
     * }
     */

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // Set the death message to empty so all players won't see it.
        e.setDeathMessage("");
        // Make sure player that died is in practice
        if (!practiceManager.isInPractice(e.getEntity().getUniqueId()))
            return;
        final Player player = e.getEntity();
        // Call the method that takes care of the death of the player
        practiceManager.handleDeath(e);
        // Ensure the killer is a player and not anyother kindof entity
        if (player.getKiller() == null || !(player.getKiller() instanceof Player))
            return;
        final Player killer = player.getKiller();
        // Now that we know who the killer is, reward them.
        if (practiceManager.isInPractice(killer.getUniqueId())) {
            // MAKE THE REWARD SYSTEM, GIVE SOMETHING TO KILLER
            killer.sendMessage("You've killed " + player.getDisplayName() + "!");
            killer.setHealth(20.0D);
            return;
        }
        killer.sendMessage(
                "A player that you damaged has died and you technically did kill him. Congratulations I guess.");

    }

    @EventHandler
    public void onBucketEvent(PlayerBucketEmptyEvent e) {
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            e.getBlockClicked().getRelative(e.getBlockFace()).setType(Material.AIR);
            e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().first(Material.BUCKET),
                    new ItemStack(e.getBucket()));
        }, 2L);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        e.setCancelled(true);
    }

}