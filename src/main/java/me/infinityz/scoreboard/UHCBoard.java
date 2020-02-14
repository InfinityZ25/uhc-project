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
    public int timer, kills, teamKills, playersLeft, teamsLeft, currentBorder, spectators;

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
        for (int i = 0; i < lineStrings.length; i++) {
            String line_name = lineStrings[i];
            if (line_name.equalsIgnoreCase("<spacer>")) {
                line_name = "§o" + toSpaceString(spacer);
                spacer++;
            }
            this.setLine(lineStrings.length - i, line_name);
        }
        // Call the instance instead of accesing it locally to avoid consuming lots of
        // ram. Add the scoreboardSign to the scoreboardManager
        UHC.getInstance().scoreboardManager.scoreboardMap.put(player.getUniqueId(), this);
    }
}
