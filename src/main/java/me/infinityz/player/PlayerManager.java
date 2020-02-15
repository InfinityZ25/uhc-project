package me.infinityz.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.infinityz.UHC;

/**
 * PlayerManager
 */
public class PlayerManager {
    public List<UHCPlayer> players;

    public PlayerManager(UHC instance) {
        players = new ArrayList<>();
    }

    public int getAlivePlayers() {
        return players.size();
    }

    public int getPlayersKills(Player player) {
        return 0;
    }

    public int getTeamsLeft() {
        return Bukkit.getOnlinePlayers().size();
    }

}