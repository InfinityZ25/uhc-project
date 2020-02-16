package me.infinityz.logic;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.UHC;

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
        Bukkit.broadcastMessage("[WorldBorder] The world will shrink to " + to_shrink_size + "x" + to_shrink_size
                + " in " + t + " minutes!");
        t--;
    }

}