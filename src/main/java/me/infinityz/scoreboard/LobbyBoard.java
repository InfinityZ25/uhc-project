package me.infinityz.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.infinityz.UHC;

/**
 * LobbyBoard
 */
public class LobbyBoard extends ScoreboardSign {
    @Getter
    @Setter
    private int host_line, player_line, scenarios_start_line;

    public LobbyBoard(@NonNull Player player, String objectiveName, @NonNull String... lineStrings) {
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
        for (int i = 0; i < lineStrings.length; i++) {
            String line_name = lineStrings[i];
            if (line_name.equalsIgnoreCase("<spacer>")) {
                line_name = "§o" + toSpaceString(spacer);
                spacer++;
            }
            if (line_name.toLowerCase().contains("<host>")) {
                this.host_line = i;
                line_name = line_name.replace("<host>", "Undefined");
            }
            if (line_name.toLowerCase().contains("<players>")) {
                this.player_line = i;
                line_name = line_name.replace("<players>", Bukkit.getOnlinePlayers().size() + "");
            }
            if (line_name.toLowerCase().contains("<scenarios>")) {
                this.scenarios_start_line = i;
                line_name = line_name.replace("<scenarios>", " - Vanilla");
            }
            this.setLine(lineStrings.length - i, line_name);
        }

        UHC.getInstance().scoreboardManager.scoreboardMap.put(player.getUniqueId(), this);
    }

}