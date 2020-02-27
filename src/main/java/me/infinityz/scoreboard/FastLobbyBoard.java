package me.infinityz.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.infinityz.UHC;
import me.infinityz.scenarios.IScenario;
import net.md_5.bungee.api.ChatColor;

/**
 * FastUHCBoard
 */
public class FastLobbyBoard extends FastBoard {
    public int host_line, player_line, scenarios_start_line, scenarios_end_line;
    public String players_line, hosts_line;

    public FastLobbyBoard(final Player player, final String objectiveName, final String... lineStrings) {
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
            if (line.toLowerCase().contains("<host>")) {
                this.host_line = line_id;
                this.hosts_line = line;
                line = line.replace("<host>",
                        UHC.getInstance().gameConfigManager.last_known_host_name.isEmpty() ? "Undefined"
                                : UHC.getInstance().gameConfigManager.last_known_host_name);
            }
            if (line.toLowerCase().contains("<players>")) {
                this.players_line = line;
                this.player_line = line_id;
                line = line.replace("<players>", Bukkit.getOnlinePlayers().size() + "");
            }
            if (line.toLowerCase().contains("<scenarios>")) {
                this.scenarios_start_line = line_id;
                if (UHC.getInstance().scenariosManager.getActiveScenarios().isEmpty()) {
                    line = line.replace("<scenarios>", " - Vanilla");
                    this.scenarios_end_line = line_id;
                } else {
                    for (IScenario scenario : UHC.getInstance().scenariosManager.getActiveScenarios()) {
                        if (UHC.getInstance().scenariosManager.getActiveScenarios().isEmpty())
                            return;
                        this.updateLine(line_id, ChatColor.translateAlternateColorCodes('&',
                                " - " + scenario.getClass().getSimpleName()));
                        line_id++;
                    }
                    this.scenarios_end_line = line_id + 1;
                    continue;
                }
            }
            this.updateLine(line_id, ChatColor.translateAlternateColorCodes('&', line));
            line_id++;
        }
        // Call the instance instead of accesing it locally to avoid consuming lots of
        // ram. Add the scoreboardSign to the scoreboardManager
        UHC.getInstance().scoreboardManager.fastBoardMap.put(player.getUniqueId(), this);
    }

}