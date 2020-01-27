package me.infinityz.teams;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * TeamManager
 */
public class TeamManager {
    public LinkedList<Team> teamList;
    public Map<UUID, Team> map;

    public TeamManager() {
        teamList = new LinkedList<>();
        map = new HashMap<>();
    }
    //Use the keyword synchronized to avoid teams being created with the same team number.
    public synchronized Team createTeam(Player player){
        Team team = new Team(player.getUniqueId(), "" + teamList.size()+1);
        teamList.add(team);
        return team;
    }

}