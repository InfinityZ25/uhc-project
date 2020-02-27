package me.infinityz.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSkeleton;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.infinityz.UHC;
import me.infinityz.logic.GameWinEvent;
import me.infinityz.logic.GameWinEvent.WinType;
import me.infinityz.player.UHCPlayer;
import me.infinityz.player.UHCPlayerDeathEvent;
import me.infinityz.player.UHCPlayerDisconnectEvent;
import me.infinityz.scoreboard.ScoreboardSign;
import me.infinityz.scoreboard.UHCBoard;
import me.infinityz.teams.objects.Team;
import net.md_5.bungee.api.ChatColor;

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
        if (uhcPlayer != null) {
            uhcPlayer.last_disconnect_time = null;
            if (uhcPlayer.alive && !uhcPlayer.spectator) {
                if (uhcPlayer.entity_combatlogger_id > -1) {
                    player.getWorld().getEntities().stream().filter(it -> it.getType() == EntityType.SKELETON)
                            .filter(it -> it.getEntityId() == uhcPlayer.entity_combatlogger_id).forEach(entity -> {
                                CraftSkeleton skeleton = (CraftSkeleton) entity;
                                player.setHealth(skeleton.getHealth());
                                player.teleport(entity);
                                entity.remove();
                            });
                }
                if (instance.playerManager.pending_respawns.contains(uhcPlayer.uuid)) {
                    player.getInventory().setContents(uhcPlayer.death_Inventory);

                    player.getInventory().setArmorContents(uhcPlayer.armour);
                    uhcPlayer.armour = null;
                    uhcPlayer.death_Inventory = null;
                    uhcPlayer.died_time = null;
                    Bukkit.getScheduler().runTaskLater(instance, () -> {
                        player.teleport(uhcPlayer.death_location);
                        instance.playerManager.pending_respawns.remove(uhcPlayer.uuid);
                        uhcPlayer.death_location = null;
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            all.showPlayer(player);
                        });
                    }, 5L);

                }
            } else {
                Bukkit.getScheduler().runTaskLater(instance, () -> {
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0.0, 2.0, 0.0));
                }, 3l);
            }
        } else {
            UHCPlayer pl = new UHCPlayer(e.getPlayer().getUniqueId());
            pl.alive = false;
            pl.spectator = false;
        }
        // TODO: Change this to keep players hidden.

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

        new UHCBoard(player, " &3Arcadens UHC &7(Test) ", "&7Timer: &f<timer>", "<spacer>",
                "&7Your kills: &f<player_kills>", "<spacer>", "&7Players Left: &f<players_left>",
                "&7Border: &f<border>", "<spacer>", "&3  Arcadens.net ");

    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e) {
        if (instance.gameLogicManager.game_pvp)
            return;
        if (e.getDamager().getType() == EntityType.PLAYER && e.getEntity().getType() == EntityType.PLAYER) {
            e.setCancelled(true);
            e.getDamager().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c Pvp is disabled!"));
        }
    }

    @EventHandler
    public void onUHCDisconnect(UHCPlayerDisconnectEvent e) {
        e.uhcPlayer.last_disconnect_time = System.currentTimeMillis();
        if (!e.uhcPlayer.alive) {
            return;
        }
        if (!instance.gameLogicManager.game_pvp) {
            // If pvp is off, then add him to a schedule that checks last time he was
            // connected to get him dq'd as soon as pvp is on!
            return;
        } /*
           * keepLoaded.add(e.playerQuitEvent.getPlayer().getLocation().getChunk());
           * Bukkit.getScheduler().runTask(UHC.getInstance(), () -> {
           * e.playerQuitEvent.getPlayer().getWorld().loadChunk(e.playerQuitEvent.
           * getPlayer().getLocation().getChunk()); EntityTypes.spawnEntity( new
           * CombatLoggerEntity(e.playerQuitEvent.getPlayer().getWorld(),
           * e.playerQuitEvent.getPlayer()), e.playerQuitEvent.getPlayer().getLocation());
           * keepLoaded.remove(e.playerQuitEvent.getPlayer().getLocation().getChunk());
           * 
           * });
           */
        // TODO: FIX COMBAT LOGGER
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        UHCPlayer uhcplayer = instance.playerManager.getUHCPlayerFromID(e.getEntity().getUniqueId());
        if (uhcplayer != null && uhcplayer.alive && !uhcplayer.spectator) {
            uhcplayer.alive = false;
            uhcplayer.last_disconnect_time = 0L;
            uhcplayer.death_Inventory = e.getEntity().getInventory().getContents();
            uhcplayer.armour = e.getEntity().getInventory().getArmorContents();
            Bukkit.getPluginManager().callEvent(new UHCPlayerDeathEvent(e, uhcplayer));
        }
        instance.whitelistManager.whitelist.remove(e.getEntity().getUniqueId());
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            e.getEntity().spigot().respawn();
            e.getEntity().kickPlayer("You've died!");
        }, 15 * 20L);
    }

    @EventHandler
    public void onUHCDeath(UHCPlayerDeathEvent e) {
        // Later detect if he was the last member of his team and delete them!
        Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
            // Code for respawn Mechanics starts here
            e.uhcPlayer.died_time = System.currentTimeMillis();
            // Maybe implement a way of getting the death combat loggers inventory too.
            e.uhcPlayer.death_location = e.entityDeathEvent == null ? e.playerDeathEvent.getEntity().getLocation()
                    : e.entityDeathEvent.getEntity().getLocation();
            // Respawn mechanics ends here
            int i = instance.playerManager.getAlivePlayers();
            int x = instance.playerManager.getTeamsLeft();
            Bukkit.getOnlinePlayers().forEach(all -> {
                ScoreboardSign sb = instance.scoreboardManager.scoreboardMap.get(all.getUniqueId());
                if (sb != null && sb instanceof UHCBoard) {
                    UHCBoard board = (UHCBoard) sb;
                    board.updatePlayersLeft(i);
                    if (instance.teamManager.team_enabled) {
                        board.updateTeamsLeft(x);
                    }
                }
            });
            Player killer = (e.entityDeathEvent == null ? e.playerDeathEvent.getEntity().getKiller()
                    : e.entityDeathEvent.getEntity().getKiller());
            if (killer != null) {
                UHCPlayer killer_UHC = instance.playerManager.getUHCPlayerFromID(killer.getUniqueId());
                if (killer_UHC == null)
                    return;
                killer_UHC.game_kills += 1;
                if (instance.teamManager.team_enabled) {
                    Team team = killer_UHC.team;
                    team.team_kills += 1;
                    if (team != null) {
                        team.team_members.forEach(all -> {
                            ScoreboardSign sb = instance.scoreboardManager.scoreboardMap.get(all);
                            if (sb != null && sb instanceof UHCBoard) {
                                UHCBoard board = (UHCBoard) sb;
                                board.updateTeamKills(team.team_kills);
                            }

                        });
                    }
                }
                ScoreboardSign sb = instance.scoreboardManager.scoreboardMap.get(killer.getUniqueId());
                if (sb != null && sb instanceof UHCBoard) {
                    UHCBoard board = (UHCBoard) sb;
                    board.updatePlayerKills(killer_UHC.game_kills);
                }
            }

            if (!instance.gameLogicManager.finished) {
                if (instance.teamManager.team_enabled) {
                    if (x <= 1) {
                        Bukkit.getPluginManager().callEvent(
                                new GameWinEvent(instance.playerManager.getFirstPlayerAlive(), WinType.TEAMS));
                        instance.gameLogicManager.finished = true;
                    }
                } else {
                    if (i <= 1) {
                        Bukkit.getPluginManager().callEvent(
                                new GameWinEvent(instance.playerManager.getFirstPlayerAlive(), WinType.SOLO));
                        instance.gameLogicManager.finished = true;
                    }
                }
            }

        });
    }

    @EventHandler
    public void onWinEvent(GameWinEvent e) {
        instance.gameLogicManager.gameLogicTask.cancel();

        if (e.winType == WinType.SOLO) {
            if (e.uhcPlayer != null) {
                Bukkit.broadcastMessage(Bukkit.getOfflinePlayer(e.uhcPlayer.uuid).getName() + " has won the UHC!");
                // Fireworks effect?
            } else {
                Bukkit.broadcastMessage("There is no winner!!!");
            }
        } else {
            if (e.uhcPlayer != null) {
                Team team = e.uhcPlayer.team;
                if (team != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String name : team.getMembersName()) {
                        stringBuilder.append(name + ", ");
                    }
                    Bukkit.broadcastMessage(stringBuilder.toString() + "have won the uhc!");
                }
            } else {
                Bukkit.broadcastMessage("There is no winner!!!");
            }

        }
    }

}