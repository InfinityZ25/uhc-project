package me.infinityz.teams.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.infinityz.UHC;
import me.infinityz.UHC.GameStage;
import me.infinityz.scenarios.events.ScenarioDisabledEvent;
import me.infinityz.scenarios.events.ScenarioEnabledEvent;
import me.infinityz.scoreboard.LobbyBoard;
import me.infinityz.scoreboard.ScoreboardSign;
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

}