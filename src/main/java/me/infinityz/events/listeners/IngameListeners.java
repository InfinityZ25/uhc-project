package me.infinityz.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.infinityz.UHC;
import me.infinityz.logic.GameWinEvent;
import me.infinityz.player.UHCPlayer;
import me.infinityz.player.UHCPlayerDeathEvent;
import me.infinityz.scoreboard.ScoreboardSign;
import me.infinityz.scoreboard.UHCBoard;
import me.infinityz.teams.objects.Team;

/**
 * IngameListeners
 */
public class IngameListeners extends SkeletonListener {

    public IngameListeners(UHC instance) {
        super(instance);
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        UHCPlayer uhcPlayer = instance.playerManager.getUHCPlayerFromID(player.getUniqueId());
        if (uhcPlayer != null && uhcPlayer.alive && !uhcPlayer.spectator) {

        } else {
            player.sendMessage("You're not a player. Cant deal with you!");
        }

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

        new UHCBoard(player, "&3Arcadens UHC", "&7Timer: &f<timer>", "<spacer>", "&7Your kills: &f<player_kills>",
                "<spacer>", "&7Players Left: &f<players_left>", "&7Border: &f<border>", "<spacer>",
                "&3  Arcadens.net ");

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {/*
                                             * final Player player = e.getPlayer(); UHCPlayer uhcPlayer =
                                             * instance.playerManager.getUHCPlayerFromID(player.getUniqueId()); if
                                             * (uhcPlayer != null && uhcPlayer.alive && !uhcPlayer.spectator) { // Only
                                             * if pvp is on! instance.combatLoggerManager.skeleton.spawn(player); }
                                             */
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e) {
        if (instance.gameLogicManager.game_pvp)
            return;
        if (e.getDamager().getType() == EntityType.PLAYER && e.getEntity().getType() == EntityType.PLAYER) {
            e.setCancelled(true);
            e.getDamager().sendMessage("Pvp is disabled!");
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        UHCPlayer uhcplayer = instance.playerManager.getUHCPlayerFromID(e.getEntity().getUniqueId());
        if (uhcplayer != null && uhcplayer.alive && !uhcplayer.spectator) {
            uhcplayer.alive = false;
            Bukkit.getPluginManager().callEvent(new UHCPlayerDeathEvent(e, uhcplayer));
        }
        instance.whitelistManager.whitelist.remove(e.getEntity().getUniqueId());
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            e.getEntity().kickPlayer("You've died!");
        }, 20L);
    }

    @EventHandler
    public void onUHCDeath(UHCPlayerDeathEvent e) {
        // Later detect if he was the last member of his team and delete them!
        int i = instance.playerManager.getAlivePlayers();
        Bukkit.getOnlinePlayers().forEach(all -> {
            ScoreboardSign sb = instance.scoreboardManager.scoreboardMap.get(all.getUniqueId());
            if (sb != null && sb instanceof UHCBoard) {
                UHCBoard board = (UHCBoard) sb;
                board.queueUpdate(board.players_left, board.players_left_line.replace("<players_left>", i + ""));
            }
        });
        if (e.playerDeathEvent.getEntity().getKiller() != null) {
            Player killer = e.playerDeathEvent.getEntity().getKiller();
            UHCPlayer killer_UHC = instance.playerManager.getUHCPlayerFromID(killer.getUniqueId());
            if (killer_UHC == null)
                return;
            killer_UHC.game_kills += 1;
            if (instance.teamManager.team_enabled) {
                Team team = instance.teamManager.findPlayersTeam(killer.getUniqueId());
                team.team_kills += 1;
                if (team != null) {
                    team.team_members.forEach(all -> {
                        ScoreboardSign sb = instance.scoreboardManager.scoreboardMap.get(all);
                        if (sb != null && sb instanceof UHCBoard) {
                            UHCBoard board = (UHCBoard) sb;
                            board.queueUpdate(board.team_kills,
                                    board.team_kills_line.replace("<team_kills>", team.team_kills + ""));
                        }

                    });
                }
            }
            ScoreboardSign sb = instance.scoreboardManager.scoreboardMap.get(killer.getUniqueId());
            if (sb != null && sb instanceof UHCBoard) {
                UHCBoard board = (UHCBoard) sb;
                board.queueUpdate(board.player_kills, board.player_kills_line.replace("<player_kills>", i + ""));
            }

        }
        if (instance.teamManager.team_enabled) {
            if (instance.playerManager.getTeamsLeft() < 2) {
                // Call win
            }

        } else {
            if (instance.playerManager.getAlivePlayers() < 2) {
                // Call the win!
                Bukkit.getPluginManager().callEvent(new GameWinEvent(instance.playerManager.getFirstPlayerAlive()));

            }

        }
    }

    @EventHandler
    public void onWinEvent(GameWinEvent e) {
        if (e.uhcPlayer != null) {
            Bukkit.broadcastMessage(Bukkit.getOfflinePlayer(e.uhcPlayer.uuid).getName() + " has won the UHC!");
            instance.gameLogicManager.gameLogicTask.cancel();
        } else {
            Bukkit.broadcastMessage("There is no winner!!!");
        }
    }

    /*
     * @EventHandler public void onEntityDeathEvent(EntityDeathEvent e) { if
     * (e.getEntity() instanceof SkeletonCombatLogger) { SkeletonCombatLogger entity
     * = (SkeletonCombatLogger) e.getEntity(); OfflinePlayer offline =
     * Bukkit.getOfflinePlayer(entity.getUniqueID());
     * Bukkit.broadcastMessage(offline.getName() + "'s Combat logger has died!");
     * e.getDrops().clear(); entity.playerInventory.forEach(all -> { if (all != null
     * && all.getType() != Material.AIR) {
     * e.getEntity().getLocation().getWorld().dropItemNaturally(e.getEntity().
     * getLocation(), all); } }); } }
     * 
     * @EventHandler public void onEntityDeathEvent(CombatLoggerDeathEvent e) {
     * OfflinePlayer of = Bukkit.getOfflinePlayer(e.skeletonCombatLogger);
     * Bukkit.broadcastMessage(of.getName() + " died!"); }
     */

}