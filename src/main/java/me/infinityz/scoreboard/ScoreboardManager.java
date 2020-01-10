package me.infinityz.scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import lombok.Getter;
import me.infinityz.UHC;

/**
 * ScoreboardManager
 */
public class ScoreboardManager {
    public Map<UUID, IScoreboardSign> scoreboardMap;
    public boolean global_update;
    @Getter
    public HashSet<String> scenariosSet;

    public ScoreboardManager() {
        scoreboardMap = new HashMap<>();
        scenariosSet = new HashSet<>();

        this.global_update = true;
        loop();
    }

    void loop() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(UHC.getInstance(), () -> {
            if (!global_update)
                return;
            scoreboardMap.forEach((uuid, sb) -> {
                sb.update();
            });
        }, 20L, 1L);
    }
}