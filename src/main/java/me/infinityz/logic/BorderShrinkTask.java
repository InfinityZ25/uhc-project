package me.infinityz.logic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.UHC;
import me.infinityz.scoreboard.ScoreboardSign;
import me.infinityz.scoreboard.UHCBoard;

/**
 * BorderShrinkTask
 */
public class BorderShrinkTask extends BukkitRunnable {
    int to_shrink_size;
    int t = 5;

    public BorderShrinkTask(int to_shrink_size) {
        this.to_shrink_size = to_shrink_size;

    }

    @Override
    public void run() {
        if (t == 1) {
            this.cancel();
            new BorderShrinkTaskSeconds(to_shrink_size).runTaskTimerAsynchronously(UHC.getInstance(), 0, 20);
            return;
        }
        Bukkit.broadcastMessage(
                ChatColor.translateAlternateColorCodes('&', "&7[WorldBorder] The world will shrink to &f"
                        + to_shrink_size + "x" + to_shrink_size + "&7 in " + t + " minutes!"));

        Bukkit.getOnlinePlayers().parallelStream().forEach(all -> {
            ScoreboardSign sb = UHC.getInstance().scoreboardManager.scoreboardMap.get(all.getUniqueId());
            String update_str = ChatColor.translateAlternateColorCodes('&',
                    UHC.getInstance().gameConfigManager.gameConfig.map_size + " &8(&a" + t + "m&8)");
            if (sb != null && sb instanceof UHCBoard) {
                UHCBoard board = (UHCBoard) sb;
                board.updateBorder(update_str, false);
            }
        });
        t--;
    }

}