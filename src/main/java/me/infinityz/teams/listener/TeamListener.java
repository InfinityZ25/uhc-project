package me.infinityz.teams.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.infinityz.UHC;
import me.infinityz.UHC.GameStage;
import me.infinityz.logic.GameStartEvent;
import me.infinityz.logic.GameStartedEvent;
import me.infinityz.logic.PlayerScatteredEvent;
import me.infinityz.logic.PreGameStartEvent;
import me.infinityz.logic.ScatterLocationsFoundEvent;
import me.infinityz.logic.ScatterTeleportCompletedEvent;
import me.infinityz.scatter.Scatter;
import me.infinityz.scatter.Teleport;
import me.infinityz.scenarios.events.ScenarioDisabledEvent;
import me.infinityz.scenarios.events.ScenarioEnabledEvent;
import me.infinityz.scoreboard.LobbyBoard;
import me.infinityz.scoreboard.ScoreboardSign;
import me.infinityz.scoreboard.UHCBoard;
import me.infinityz.teams.events.TeamDisbandedEvent;
import me.infinityz.teams.events.TeamJoinedEvent;
import me.infinityz.teams.events.TeamKickedEvent;
import me.infinityz.teams.events.TeamLeftEvent;
import me.infinityz.teams.events.TeamRemovedEvent;
import me.infinityz.teams.objects.Team;

/**
 * TeamListener
 */
public class TeamListener implements Listener {

    @EventHandler
    public void teamJoinEvent(TeamJoinedEvent e) {
        final Player player = e.getPlayer();
        final Team team = e.getTeam();
        team.team_members.forEach(uuid -> {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl != null && pl.isOnline()) {
                ScoreboardSign plSign = UHC.getInstance().scoreboardManager.scoreboardMap.get(pl.getUniqueId());
                if (plSign != null) {
                    if (pl != player) {
                        plSign.getPlayer().sendPacket(ScoreboardSign.add3Remove4(3, player.getName(), "0001"));
                        plSign.updatePlayerOrder(pl);
                    } else {
                        team.team_members.stream().filter(it -> it != player.getUniqueId()).forEach(n -> {
                            Player plagain = Bukkit.getPlayer(n);
                            if (plagain != null && plagain.isOnline()) {
                                plSign.getPlayer().sendPacket(ScoreboardSign.add3Remove4(3, plagain.getName(), "0001"));
                                plSign.updatePlayerOrder(plagain);
                            }

                        });
                    }
                }
            }
        });
    }

    @EventHandler
    public void teamLeftEvent(TeamLeftEvent e) {
        final Player kicked = e.getPlayer();
        final ScoreboardSign kickedBoard = UHC.getInstance().scoreboardManager.scoreboardMap.get(kicked.getUniqueId());
        final Team team = e.getTeam();
        team.team_members.forEach(it -> {
            Player member = Bukkit.getPlayer(it);
            if (member != null && member.isOnline()) {
                ScoreboardSign memberBoard = UHC.getInstance().scoreboardManager.scoreboardMap
                        .get(member.getUniqueId());
                if (memberBoard != null) {
                    memberBoard.getPlayer().sendPacket(ScoreboardSign.add3Remove4(3, kicked.getName(), "1111"));
                    memberBoard.updatePlayerOrder(kicked);
                }
                if (kickedBoard != null) {
                    kickedBoard.getPlayer().sendPacket(ScoreboardSign.add3Remove4(3, member.getName(), "1111"));
                    kickedBoard.updatePlayerOrder(member);

                }
            }
        });
    }

    @EventHandler
    public void teamRemovedEvent(TeamRemovedEvent e) {
        final Team team = e.getTeam();
        team.team_members.forEach(it -> {
            Player member = Bukkit.getPlayer(it);
            if (member != null && member.isOnline()) {
                member.sendMessage("Your team has been removed!");
                ScoreboardSign memberBoard = UHC.getInstance().scoreboardManager.scoreboardMap
                        .get(member.getUniqueId());
                if (memberBoard != null) {
                    team.team_members.stream().filter(again -> again != it).forEach(again -> {
                        Player pl = Bukkit.getPlayer(again);
                        if (pl != null && pl.isOnline()) {
                            memberBoard.getPlayer().sendPacket(ScoreboardSign.add3Remove4(3, pl.getName(), "1111"));
                            memberBoard.updatePlayerOrder(pl);
                        }
                    });
                }
            }
        });
    }

    @EventHandler
    public void teamDisbandedEvent(TeamDisbandedEvent e) {
        final Team team = e.getTeam();
        team.team_members.forEach(it -> {
            Player member = Bukkit.getPlayer(it);
            if (member != null && member.isOnline()) {
                ScoreboardSign memberBoard = UHC.getInstance().scoreboardManager.scoreboardMap
                        .get(member.getUniqueId());
                if (memberBoard != null) {
                    team.team_members.stream().filter(again -> again != it).forEach(again -> {
                        Player pl = Bukkit.getPlayer(again);
                        if (pl != null && pl.isOnline()) {
                            memberBoard.getPlayer().sendPacket(ScoreboardSign.add3Remove4(3, pl.getName(), "1111"));
                            memberBoard.updatePlayerOrder(pl);
                        }
                    });
                }
            }
        });
    }

    @EventHandler
    public void teamRemoved(TeamKickedEvent e) {
        final Player kicked = e.getPlayer();
        final ScoreboardSign kickedBoard = UHC.getInstance().scoreboardManager.scoreboardMap.get(kicked.getUniqueId());
        final Team team = e.getTeam();
        team.team_members.forEach(it -> {
            Player member = Bukkit.getPlayer(it);
            if (member != null && member.isOnline()) {
                ScoreboardSign memberBoard = UHC.getInstance().scoreboardManager.scoreboardMap
                        .get(member.getUniqueId());
                if (memberBoard != null) {
                    memberBoard.getPlayer().sendPacket(ScoreboardSign.add3Remove4(3, kicked.getName(), "1111"));
                    memberBoard.updatePlayerOrder(kicked);
                }
                if (kickedBoard != null) {
                    kickedBoard.getPlayer().sendPacket(ScoreboardSign.add3Remove4(3, member.getName(), "1111"));
                    kickedBoard.updatePlayerOrder(member);

                }
            }
        });

    }

    @EventHandler
    public void enableScenarios(ScenarioEnabledEvent e) {
        if (GameStage.stage != GameStage.LOBBY)
            return;
        Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(all -> {
                ScoreboardSign sb = UHC.getInstance().scoreboardManager.scoreboardMap.get(all.getUniqueId());
                if (sb != null) {
                    sb.destroy();
                    UHC.getInstance().scoreboardManager.scoreboardMap.remove(all.getUniqueId());
                }
                new LobbyBoard(all, "&bArcadens UHC", "&7Host: &f<host>", "<spacer>", "&7Players: &f<players>",
                        "<spacer>", "&7Scenarios:", "<scenarios>", "<spacer>", "&b  Arcadens.net ");

            });
        });
    }

    @EventHandler
    public void enableScenarios(ScenarioDisabledEvent e) {
        if (GameStage.stage != GameStage.LOBBY)
            return;
        Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(all -> {
                ScoreboardSign sb = UHC.getInstance().scoreboardManager.scoreboardMap.get(all.getUniqueId());
                if (sb != null) {
                    sb.destroy();
                    UHC.getInstance().scoreboardManager.scoreboardMap.remove(all.getUniqueId());
                }
                new LobbyBoard(all, "&bArcadens UHC", "&7Host: &f<host>", "<spacer>", "&7Players: &f<players>",
                        "<spacer>", "&7Scenarios:", "<scenarios>", "<spacer>", "&b  Arcadens.net ");

            });
        });
    }

    @EventHandler
    public void onPreGame(PreGameStartEvent e) {
        // Handle pregame
        // Obtain Scatter Locations (sync)
        // Change 69 for the amount of players online plus a few more
        new Scatter(Bukkit.getWorld("UHC"), UHC.getInstance().gameConfigManager.gameConfig.map_size, 100,
                Bukkit.getOfflinePlayers().length + 5, 50).runTaskTimer(UHC.getInstance(), 0, 5L);
        Bukkit.broadcastMessage("Calcuating scatter locations...");

        // Teleport Players to scatter locations (sync)
        // Give them the initial loot
        // Prohibit them from moving until everyone is scattered

        // Unfreeze everyone
        // Heal and feed all players
        // Change their scoreboard to an InGame Scoreboard
        // Change the game stage to ingame

        // Run the game Tick or game Loop to start counting
    }

    @EventHandler
    public void onLocationsFound(ScatterLocationsFoundEvent e) {
        new Teleport(UHC.getInstance(), e.getLocations(), 100).runTaskTimer(UHC.getInstance(), 0, 10);
        Bukkit.broadcastMessage("All scatter locations have been found, teleporting!");

    }

    @EventHandler
    public void teleportCompleted(ScatterTeleportCompletedEvent e) {
        Bukkit.broadcastMessage("Scatter has been completed!");

        Bukkit.getPluginManager().callEvent(new GameStartEvent());
    }

    @EventHandler
    public void gameStartEvent(GameStartEvent e) {
        // Maybe write code here to wait till tps are stables to start?
        // Also instantiate the players as UHCPlayers and not spectators here.

        Bukkit.getPluginManager().callEvent(new GameStartedEvent());
    }

    @EventHandler
    public void gameStartedEvent(GameStartedEvent e) {
        Bukkit.broadcastMessage("Game has officially started!");
        UHC.getInstance().scoreboardManager.scoreboardMap.forEach((uuid, sb) -> {
            sb.destroy();
        });
        UHC.getInstance().scoreboardManager.scoreboardMap.clear();
        Bukkit.getOnlinePlayers()
                .forEach(player -> new UHCBoard(player, "&bArcadens UHC", "&7Timer: &f<timer>", "<spacer>",
                        "&7Your kills: &f<player_kills>", "<spacer>", "&7Players Left: &f<players_left>",
                        "&7Border: &f<border>", "<spacer>", "&b  Arcadens.net "));
        // If we reach this point, the game has officialy started
    }

    @EventHandler
    public void playerScattered(PlayerScatteredEvent e) {
        e.player.sendMessage("You've been scattered!");
    }

}