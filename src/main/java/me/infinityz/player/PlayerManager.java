package me.infinityz.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.infinityz.UHC;
import me.infinityz.teams.objects.Team;
import me.infinityz.whitelist.objects.NoDuplicatesList;

/**
 * PlayerManager
 */
public class PlayerManager {
    public Map<UUID, UHCPlayer> players;
    public UHC instance;

    public PlayerManager(UHC instance) {
        players = new HashMap<>();
    }

    public int getAlivePlayers() {
        int player_count = 0;
        for (UHCPlayer uhcPlayer : players.values()) {
            if (uhcPlayer.alive && !uhcPlayer.spectator)
                player_count++;
        }
        return player_count;
    }

    public UHCPlayer getFirstPlayerAlive() {

        for (UHCPlayer uhcplayer : players.values()) {
            if (!uhcplayer.alive)
                continue;
            return uhcplayer;

        }

        return null;
    }

    public int getPlayersKills(UUID uuid) {
        final UHCPlayer uhcPlayer = getUHCPlayerFromID(uuid);
        if (uhcPlayer != null) {
            return uhcPlayer.game_kills;
        }
        return 0;
    }

    public UHCPlayer getUHCPlayerFromID(UUID uuid) {
        return players.get(uuid);
    }

    public int getTeamsLeft() {
        if (!instance.teamManager.team_enabled)
            return -1;

        NoDuplicatesList<Team> teams = new NoDuplicatesList<>();

        for (UHCPlayer uhcPlayer : players.values()) {
            if (uhcPlayer.alive && uhcPlayer.team != null)
                teams.add(uhcPlayer.team);
        }

        return teams.size();
    }

}