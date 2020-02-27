package me.infinityz.scoreboard;

import org.bukkit.entity.Player;

import me.infinityz.UHC;
import net.md_5.bungee.api.ChatColor;

/**
 * FastUHCBoard
 */
public class FastUHCBoard extends FastBoard {

    public int timer, player_kills, players_left, team_kills, team_left, border, spectators;
    public String timer_line, player_kills_line, players_left_line, team_kills_line, team_left_line, border_line;

    public FastUHCBoard(final Player player, final String objectiveName, final String... lineStrings) {
        super(player);
        this.updateTitle(ChatColor.translateAlternateColorCodes('&', objectiveName));
        // Loop through all the lines and create them. Add i+1 to make the numbers look
        // prettier
        // Adding the capability to use the string <spacer> as a line separator.
        // Use a integer to keep track of the amount of spacer
        int spacer = 0;
        int line_id = 0;
        for (String line : lineStrings) {
            if (line.equalsIgnoreCase("<spacer>")) {
                line = "§o" + toSpaceString(spacer);
                spacer++;
            }
            if (line.toLowerCase().contains("<timer>")) {
                this.timer = line_id;
                this.timer_line = line;
                line = line.replace("<timer>", formatTime(UHC.getInstance().gameLogicManager.game_time));
            }
            if (line.toLowerCase().contains("<player_kills>")) {
                this.player_kills = line_id;
                this.player_kills_line = line;
                line = line.replace("<player_kills>",
                        UHC.getInstance().playerManager.getPlayersKills(player.getUniqueId()) + "");
            }
            if (line.toLowerCase().contains("<players_left>")) {
                this.players_left = line_id;
                this.players_left_line = line;
                line = line.replace("<players_left>", UHC.getInstance().playerManager.getAlivePlayers() + "");
            }
            if (line.toLowerCase().contains("<team_kills>")) {
                this.team_kills = line_id;
                this.team_kills_line = line;
                line = line.replace("<team_kills>", UHC.getInstance().playerManager.getTeamsLeft() + "");
            }
            if (line.toLowerCase().contains("<teams_left>")) {
                this.team_left = line_id;
                this.team_left_line = line;
                line = line.replace("<teams_left>", UHC.getInstance().playerManager.getTeamsLeft() + "");
            }
            if (line.toLowerCase().contains("<border>")) {
                this.border = line_id;
                this.border_line = line;
                line = line.replace("<border>", UHC.getInstance().gameConfigManager.gameConfig.map_size + "");
            }
            this.updateLine(line_id, ChatColor.translateAlternateColorCodes('&', line));
            line_id++;
        }
        // Call the instance instead of accesing it locally to avoid consuming lots of
        // ram. Add the scoreboardSign to the scoreboardManager
        UHC.getInstance().scoreboardManager.fastBoardMap.put(player.getUniqueId(), this);
    }

}