package me.infinityz.scoreboard;

import org.bukkit.entity.Player;

import me.infinityz.UHC;

public class UHCBoard extends ScoreboardSign {
    /*
     * These are all the values that we need. I could use a HashMap but that
     * consumes a lot of ram; it's way better to just use ints. The integers are not
     * initialized and public to avoid writting boiler plate code and not repeat
     * myself.
     */
    public int timer, player_kills, players_left, team_kills, team_left, border, spectators;
    public String timer_line, player_kills_line, players_left_line, team_kills_line, team_left_line, border_line;

    public UHCBoard(final Player player, final String objectiveName, final String... lineStrings) {
        super(player, objectiveName);

        // Assert the lines aren't empty
        assert lineStrings.length > 0;
        // Call the create method, it needs to be called before lines are set at least
        // once.
        this.create();
        // Loop through all the lines and create them. Add i+1 to make the numbers look
        // prettier
        // Adding the capability to use the string <spacer> as a line separator.
        // Use a integer to keep track of the amount of spacer
        int spacer = 0;
        int line_id = lineStrings.length;
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
            this.setLine(line_id, line);
            line_id--;
        }
        // Call the instance instead of accesing it locally to avoid consuming lots of
        // ram. Add the scoreboardSign to the scoreboardManager
        UHC.getInstance().scoreboardManager.scoreboardMap.put(player.getUniqueId(), this);
    }

    public void updatePlayerKills(int new_kills) {
        super.queueUpdate(player_kills, player_kills_line.replace("<player_kills>", new_kills + ""));
    }

    public void updateTeamKills(int new_team_kills) {
        super.queueUpdate(team_kills, team_kills_line.replace("<team_kills>", team_kills + ""));
    }

    public void updatePlayersLeft(int new_players_left) {
        super.queueUpdate(players_left, players_left_line.replace("<players_left>", new_players_left + ""));
    }

    public void updateTeamsLeft(int new_teams_left) {
        super.queueUpdate(team_left, team_left_line.replace("<teams_left>", new_teams_left + ""));
    }

    public void updateTimer(int new_timer_seconds, boolean force) {
        if (force) {
            super.forceUpdate(timer, timer_line.replace("<timer>", formatTime(new_timer_seconds)));
            return;
        }
        super.queueUpdate(timer, timer_line.replace("<timer>", formatTime(new_timer_seconds)));
    }

    public void updateBorder(String new_border, boolean force) {
        if (force) {
            super.forceUpdate(border, border_line.replace("<border>", new_border));
            return;
        }
        super.queueUpdate(border, border_line.replace("<border>", new_border));
    }

}
