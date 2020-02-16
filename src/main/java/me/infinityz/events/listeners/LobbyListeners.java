package me.infinityz.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.infinityz.UHC;
import me.infinityz.scoreboard.LobbyBoard;
import me.infinityz.scoreboard.ScoreboardSign;
import me.infinityz.teams.objects.Team;
import net.md_5.bungee.api.ChatColor;

/**
 * LobbyListeners
 */
public class LobbyListeners extends SkeletonListener {

    private Location spawnLocation;
    private String joinMessage;

    public LobbyListeners(UHC instance) {
        super(instance);
        this.spawnLocation = Bukkit.getWorlds().get(0).getSpawnLocation().add(0.0, 2.0, 0.0);
        this.joinMessage = ChatColor.translateAlternateColorCodes('&', "&6Welcome to Arcadens UHC!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // This would take place once login has been granted and player has successfully
        // made it through all possible connection barriers (Whitelist, Ban, etc).
        final Player player = e.getPlayer();
        // Teleport player to spawn and send motd.
        player.teleport(spawnLocation);
        player.sendMessage(joinMessage);
        // Reset player's inventory, health, effects, and hunger.
        player.setHealth(20D);
        player.getInventory().setArmorContents(null);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().forEach(effect -> {
            player.removePotionEffect(effect.getType());
        });
        // Start sending the scoreboard in a asynchronus fashion to prevent overload.
        Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
            Bukkit.getOnlinePlayers().stream().filter(it -> player != it).forEach(it -> {
                ScoreboardSign sign = instance.scoreboardManager.scoreboardMap.get(it.getUniqueId());
                if (sign != null) {
                    sign.updatePlayerOrder(player);
                    sign.getPlayer().sendPacket(ScoreboardSign.add3Remove4(3, player.getName(), "1111"));
                }
            });

            Team team = instance.teamManager.findPlayersTeam(player.getUniqueId());
            if (team != null) {
                team.team_members.forEach(it -> {
                    Player pl = Bukkit.getPlayer(it);
                    if (pl != null && pl.isOnline()) {
                        if (pl.getUniqueId() != player.getUniqueId()) {
                            ScoreboardSign sign = instance.scoreboardManager.scoreboardMap.get(pl.getUniqueId());
                            if (sign != null) {
                                sign.getPlayer().sendPacket(ScoreboardSign.add3Remove4(3, player.getName(), "0001"));
                                sign.updatePlayerOrder(pl);
                            }
                        }
                    }
                });
            }

        });

        new LobbyBoard(player, "&3Arcadens UHC", "&7Host: &f<host>", "<spacer>", "&7Players: &f<players>", "<spacer>",
                "&7Scenarios:", "<scenarios>", "<spacer>", "&3  Arcadens.net ");
    }

    @EventHandler
    public void onBreakEvent(BlockBreakEvent e) {
        if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby"))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreakEvent(BlockPlaceEvent e) {
        if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby"))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!e.getEntity().getWorld().getName().equalsIgnoreCase("lobby"))
            return;
        e.setCancelled(true);
        final Player player = (Player) e.getEntity();
        player.setSaturation(1.0F);
        player.setFoodLevel(20);
    }

    @EventHandler
    public void entitySpawn(EntitySpawnEvent e) {
        if (!e.getLocation().getWorld().getName().equalsIgnoreCase("lobby"))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void interactEvent(PlayerInteractEvent e) {
        if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby"))
            return;
        if (e.getAction() == Action.PHYSICAL) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent e) {
        if (e.getEntityType() != EntityType.PLAYER)
            return;
        if (!e.getEntity().getWorld().getName().equalsIgnoreCase("lobby"))
            return;
        e.setCancelled(true);
    }

}