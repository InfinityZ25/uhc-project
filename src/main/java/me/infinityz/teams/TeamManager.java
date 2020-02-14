package me.infinityz.teams;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.infinityz.UHC;
import me.infinityz.teams.commands.TeamCommand;
import me.infinityz.teams.listener.TeamListener;
import me.infinityz.teams.objects.Team;
import me.infinityz.teams.objects.TeamInvite;
import me.infinityz.whitelist.objects.NoDuplicatesList;;

/**
 * TeamManager
 */
public class TeamManager {
    public LinkedList<Team> teamList;
    public List<TeamInvite> teamInvites;
    public Map<UUID, Team> map;
    public int team_size;
    public boolean team_enabled, team_management;

    public TeamManager(UHC instance) {
        teamList = new LinkedList<>();
        teamInvites = new NoDuplicatesList<>();
        map = new HashMap<>();
        team_size = 2;
        team_enabled = false;
        team_management = false;
        TeamCommand teamCommand = new TeamCommand(this);
        instance.getCommand("team").setExecutor(teamCommand);
        instance.getCommand("team").setTabCompleter(teamCommand);
        Bukkit.getPluginManager().registerEvents(new TeamListener(), instance);
    }

    // Use the keyword synchronized to avoid teams being created with the same team
    // number.
    public synchronized Team createTeam(Player player, String team_name) {
        Team team = new Team(player.getUniqueId(), team_name);
        teamList.add(team);
        map.put(player.getUniqueId(), team);
        return team;
    }

    // Use the keyword synchronized to avoid teams being created with the same team
    // number.
    public synchronized Team createTeam(Player player) {
        return createTeam(player, "" + (teamList.size() + 1));
    }

    public Team findPlayersTeam(UUID player) {
        return map.get(player);
    }

    public TeamInvite createInvite(Team team, UUID sender, UUID target) {
        TeamInvite invite = new TeamInvite(team, sender, target);
        teamInvites.add(invite);
        return invite;
    }

    public TeamInvite getInvite(UUID player, UUID target) {
        for (TeamInvite invite : teamInvites) {
            if (player == invite.target && target == invite.sender) {
                return invite;
            }
        }
        return null;
    }

}