package me.infinityz.scatter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.UHC;
import me.infinityz.logic.PlayerScatteredEvent;
import me.infinityz.logic.ScatterTeleportCompletedEvent;
import me.infinityz.teams.objects.Team;

/**
 * Teleport
 */
public class Teleport extends BukkitRunnable {
    UHC instance;
    List<Location> locations;
    List<Player> players;
    int delay_ms;

    public Teleport(UHC instance, List<Location> locations, int delay) {
        this.instance = instance;
        this.locations = locations;
        this.delay_ms = delay;
        // Quickly clone the players so that handling is easier and returns no
        // concurrentModificationErrors.
        this.players = new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    @Override
    public void run() {
        long milliseconds = System.currentTimeMillis();
        for (Player player : new ArrayList<>(players)) {
            if (milliseconds + delay_ms <= System.currentTimeMillis()) {
                Bukkit.broadcastMessage(this.players.size() + " players left to scatter!");

                return;
            }
            if (player == null || !player.isOnline()) {
                players.remove(player);
                // Maybe queue it up for when the player logs back in?
                return;
            }
            // Do the actually teleportation magic!
            player.teleport(this.locations.get(0));
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
            // UHC.getInstance().sit(player);
            Bukkit.getPluginManager().callEvent(new PlayerScatteredEvent(player));
            // Check if it is a teams game.
            if (instance.teamManager.team_enabled) {
                // Check if the player has a team
                Team team = instance.teamManager.findPlayersTeam(player.getUniqueId());
                if (team != null) {
                    // Check if his team mates are online
                    team.team_members.stream().filter(uuid -> player.getUniqueId() != uuid).forEach(members -> {
                        Player member = Bukkit.getPlayer(members);
                        if (member != null && member.isOnline()) {
                            // Teleport the mates and remove them from the list
                            member.teleport(this.locations.get(0));
                            member.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
                            Bukkit.getPluginManager().callEvent(new PlayerScatteredEvent(member));
                            players.remove(member);
                        }
                    });
                }
            }
            locations.remove(this.locations.get(0));
            players.remove(player);

        } /*
           * new ArrayList<>(players).forEach(player -> { if (milliseconds + delay_ms <=
           * System.currentTimeMillis()) { Bukkit.broadcastMessage(this.players.size() +
           * " players left to scatter!");
           * 
           * return; } if (player == null || !player.isOnline()) { players.remove(player);
           * // Maybe queue it up for when the player logs back in? return; } // Do the
           * actually teleportation magic! player.teleport(this.locations.get(0));
           * player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
           * Bukkit.getPluginManager().callEvent(new PlayerScatteredEvent(player)); //
           * Check if it is a teams game. if (instance.teamManager.team_enabled) { //
           * Check if the player has a team Team team =
           * instance.teamManager.findPlayersTeam(player.getUniqueId()); if (team != null)
           * { // Check if his team mates are online
           * team.team_members.stream().filter(uuid -> player.getUniqueId() !=
           * uuid).forEach(members -> { Player member = Bukkit.getPlayer(members); if
           * (member != null && member.isOnline()) { // Teleport the mates and remove them
           * from the list member.teleport(this.locations.get(0));
           * member.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
           * Bukkit.getPluginManager().callEvent(new PlayerScatteredEvent(member));
           * players.remove(member); } }); } } locations.remove(this.locations.get(0));
           * players.remove(player); });
           */

        if (players.isEmpty()) {
            // Call teleportCompleteEvent
            Bukkit.getPluginManager().callEvent(new ScatterTeleportCompletedEvent());
            this.cancel();
        }

    }

}