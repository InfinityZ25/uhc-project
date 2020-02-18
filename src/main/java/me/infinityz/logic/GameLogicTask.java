package me.infinityz.logic;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.UHC;
import me.infinityz.scoreboard.ScoreboardSign;
import me.infinityz.scoreboard.UHCBoard;
import net.md_5.bungee.api.ChatColor;

/**
 * GameLogicTask
 */
public class GameLogicTask extends BukkitRunnable {
    GameLogicManager gameLogicManager;
    UHC instance;

    public GameLogicTask(GameLogicManager gameLogicManager) {
        this.gameLogicManager = gameLogicManager;
        this.instance = UHC.getInstance();
    }

    @Override
    public void run() {
        gameLogicManager.game_time++;
        Bukkit.getOnlinePlayers().stream().forEach(player -> {
            ScoreboardSign sb = instance.scoreboardManager.scoreboardMap.get(player.getUniqueId());
            if (sb != null && sb instanceof UHCBoard) {
                UHCBoard board = (UHCBoard) sb;
                board.updateTimer(gameLogicManager.game_time, false);
            }
        });
        checkForGameEvent(gameLogicManager.game_time);
    }

    void checkForGameEvent(int second) {
        if (second == instance.gameConfigManager.gameConfig.border_time) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    "\n&7[WorldBorder] The Worldboder will shrink 500 blocks every 5 minutes until it reaches 100x100.\n[WorldBorder] Then it will shrink to half of the border size every 5 minutes until 10x10\n "));
            new BorderShrinkTask(getShrinkSize(instance.gameConfigManager.gameConfig.map_size))
                    .runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);

        }
        if (second == instance.gameConfigManager.gameConfig.final_heal_time) {
            Bukkit.getOnlinePlayers().forEach(all -> {
                all.setHealth(20.0D);
            });
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "\n&cThat was your final heal!\n "));
        }
        if (second == instance.gameConfigManager.gameConfig.pvp_time) {
            gameLogicManager.game_pvp = true;
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "\n&cPvp has been enabled!\n "));
        }

    }

    int getShrinkSize(int current_map_size) {
        if (current_map_size > 2500) {
            return 2500;
        } else if (current_map_size > 2000) {
            return 2000;
        } else if (current_map_size > 1500) {
            return 1500;
        } else if (current_map_size > 1000) {
            return 1000;
        } else if (current_map_size > 500) {
            return 500;
        } else if (current_map_size > 100) {
            return 100;
        } else {
            return 50;
        }
    }

}