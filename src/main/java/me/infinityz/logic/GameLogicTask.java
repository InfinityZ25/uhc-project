package me.infinityz.logic;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.UHC;
import me.infinityz.scoreboard.ScoreboardSign;
import me.infinityz.scoreboard.UHCBoard;

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
                board.queueUpdate(board.timer,
                        board.timer_line.replace("<timer>", board.formatTime(gameLogicManager.game_time)));
            }
        });
        checkForGameEvent(gameLogicManager.game_time);
    }

    void checkForGameEvent(int second) {
        if (second == instance.gameConfigManager.gameConfig.border_time) {
            Bukkit.broadcastMessage(
                    "[WorldBorder] The Worldboder will shrink 500 blocks every 5 minutes until it reaches 100x100.\n[WorldBorder] Then it will shrink to half of the border size every 5 minutes until 10x10");
            new BorderShrinkTask(1500).runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);

        }
        if (second == instance.gameConfigManager.gameConfig.final_heal_time) {
            Bukkit.getOnlinePlayers().forEach(all -> {
                all.setHealth(20.0D);
            });
            Bukkit.broadcastMessage("That was your final heal!");
        }
        if (second == instance.gameConfigManager.gameConfig.pvp_time) {
            gameLogicManager.game_pvp = true;
            Bukkit.broadcastMessage("Pvp has been enabled!");
        }

    }

}