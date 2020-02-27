package me.infinityz.logic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.UHC;
import me.infinityz.border.BedrockBorderTask;
import me.infinityz.scoreboard.ScoreboardSign;
import me.infinityz.scoreboard.UHCBoard;
import net.md_5.bungee.api.ChatColor;

/**
 * BorderShrinkTask
 */
public class BorderShrinkTaskSeconds extends BukkitRunnable {
    int to_shrink_size;
    int t = 60;

    public BorderShrinkTaskSeconds(int to_shrink_size) {
        this.to_shrink_size = to_shrink_size;

    }

    void randomTp(Player player, int size) {
        Bukkit.getScheduler().runTask(UHC.getInstance(), () -> {
            Location loc = new Location(Bukkit.getWorld("UHC"), 0, 0, 0);
            loc.setX(loc.getX() + Math.random() * size * 2.0 - size);
            loc.setZ(loc.getZ() + Math.random() * size * 2.0 - size);
            player.teleport(loc.getWorld().getHighestBlockAt(loc).getLocation().add(0.0, 2.0, 0.0));
        });
    }

    @Override
    public void run() {
        if (t == 0) {
            this.cancel();
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6[WorldBorder] The world was shrunk to &f" + to_shrink_size + "x" + to_shrink_size + "!"));
            if (to_shrink_size == 500) {
                UHC.getInstance().gameConfigManager.gameConfig.nether = false;
                Bukkit.getOnlinePlayers().stream().filter(it -> it.getWorld().getEnvironment() == Environment.NETHER)
                        .forEach(all -> {
                            randomTp(all, 498);
                        });
            }
            new BedrockBorderTask(Bukkit.getWorld("UHC"), to_shrink_size, 4, 50).runTaskTimer(UHC.getInstance(), 0, 5);
            UHC.getInstance().gameConfigManager.gameConfig.map_size = to_shrink_size;
            Bukkit.getOnlinePlayers().parallelStream().forEach(all -> {
                ScoreboardSign sb = UHC.getInstance().scoreboardManager.scoreboardMap.get(all.getUniqueId());
                ;
                if (sb != null && sb instanceof UHCBoard) {
                    UHCBoard board = (UHCBoard) sb;
                    board.updateBorder(to_shrink_size + "", false);
                }
            });
            Bukkit.getScheduler().runTask(UHC.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "wb UHC set " + to_shrink_size + " " + to_shrink_size + " 0 0");
            });

            switch (to_shrink_size) {
                case 3000: {
                    new BorderShrinkTask(2500).runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);
                    // Shrink to 2000
                    break;
                }
                case 2500: {
                    new BorderShrinkTask(2000).runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);
                    // Shrink to 2000
                    break;
                }
                case 2000: {
                    new BorderShrinkTask(1500).runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);
                    // Shrink to 1500
                    break;
                }
                case 1500: {
                    new BorderShrinkTask(1000).runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);
                    // Shrink to 1000
                    break;
                }
                case 1000: {
                    new BorderShrinkTask(500).runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);
                    // Shrink to 500, randomtp outside border
                    break;
                }
                case 500: {
                    new BorderShrinkTask(100).runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);
                    // Shrink to 100
                    break;
                }
                case 100: {
                    new BorderShrinkTask(50).runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);
                    // Shrink to 50
                    break;
                }
                case 50: {
                    new BorderShrinkTask(25).runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);
                    // Shrink to 25
                    break;
                }
                case 25: {
                    new BorderShrinkTask(10).runTaskTimerAsynchronously(UHC.getInstance(), 0L, 20 * 60);
                    // Shrink to 10
                    break;
                }
                case 10: {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            "&7[WorldBorder] The world has shrunk to its final size!"));
                    break;
                }
            }
            return;
        }
        switch (t) {
            case 60:
            case 30:
            case 15:
            case 10:
            case 5:
            case 4:
            case 3:
            case 2: {
                Bukkit.broadcastMessage(
                        ChatColor.translateAlternateColorCodes('&', "&7[WorldBorder] The world will shrink to &f"
                                + to_shrink_size + "x" + to_shrink_size + "&7 in " + t + " seconds!"));
                break;
            }
            case 1: {
                Bukkit.broadcastMessage(
                        ChatColor.translateAlternateColorCodes('&', "&7[WorldBorder] The world will shrink to &f"
                                + to_shrink_size + "x" + to_shrink_size + "&7 in " + t + " second!"));
                break;
            }
        }
        String update_str = ChatColor.translateAlternateColorCodes('&',
                UHC.getInstance().gameConfigManager.gameConfig.map_size + " &8(&a" + t + "s&8)");
        Bukkit.getOnlinePlayers().parallelStream().forEach(all -> {
            ScoreboardSign sb = UHC.getInstance().scoreboardManager.scoreboardMap.get(all.getUniqueId());
            if (sb != null && sb instanceof UHCBoard) {
                UHCBoard board = (UHCBoard) sb;
                board.updateBorder(update_str, false);
            }
        });
        t--;
    }

}