package me.infinityz.teams;

import java.util.*;

import javax.annotation.Nonnull;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Team
 */
public class Team {
    // Possibly use an array instead of a List to save memory.
    private List<UUID> members;
    private UUID teamLeader;
    private String team_name;

    // Initialize the team with the name and the array of players that'll be part of
    // the team. Add non-null check later
    @Nonnull
    public Team(String team_name, Player... players) {
        this.team_name = team_name;
        this.members = new ArrayList<>();
        // Might cause exception, check later.
        while (players.length > 0) {
            this.members.add(players[0].getUniqueId());
            ArrayUtils.remove(players, 0);
        }
    }

    // Get online team mates, use as a notifier method.
    public List<Player> getOnlineMates() {
        List<Player> players = new ArrayList<>();
        members.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline())
                return;
            players.add(player);
        });
        return players;
    }

    public void changeTeamName(String str) {

    }

    // Get name of all team members

}