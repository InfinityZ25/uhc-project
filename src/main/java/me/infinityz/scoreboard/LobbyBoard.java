package me.infinityz.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.infinityz.UHC;
import me.infinityz.scenarios.IScenario;

/**
 * LobbyBoard
 */
public class LobbyBoard extends ScoreboardSign {
    @Getter
    @Setter
    private int host_line, player_line, scenarios_start_line, scenarios_end_line;
    private String players_line, hosts_line;

    @Override
    public void update() {
        super.queueUpdate(player_line, players_line.replace("<players>", Bukkit.getOnlinePlayers().size() + ""));
        super.queueUpdate(host_line,
                hosts_line.replace("<host>",
                        UHC.getInstance().gameConfigManager.last_known_host_name.isEmpty() ? "Undefined"
                                : UHC.getInstance().gameConfigManager.last_known_host_name));
        super.update();
        // Bukkit.broadcastMessage("message");
        // Important: #getClass()#getSimpleName() returns a string with the name of the
        // class that can be used to recognized what needs to be updated.

    }

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
        int added_lines = UHC.getInstance().scenariosManager.getActiveScenarios().size();
        int line_id = lineStrings.length + (added_lines > 0 ? added_lines - 1 : added_lines);
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
                        this.setLine(line_id, " - " + scenario.getClass().getSimpleName());
                        line_id--;
                    }
                    this.scenarios_end_line = line_id + 1;
                    continue;
                }
            }
            this.setLine(line_id, line);
            line_id--;
        }
        UHC.getInstance().scoreboardManager.scoreboardMap.put(player.getUniqueId(), this);
    }
}
