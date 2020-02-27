package me.infinityz.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.infinityz.UHC;

/**
 * ScoreboardManager
 */
public class ScoreboardManager {
    public Map<UUID, ScoreboardSign> scoreboardMap;
    public Map<UUID, FastBoard> fastBoardMap;
    public boolean global_update;

    public ScoreboardManager() {
        scoreboardMap = new HashMap<>();
        fastBoardMap = new HashMap<>();

        this.global_update = true;
        loop();
    }

    void loop() {
        // Doing some manual house keeping here
        UHC.getInstance().executorService.scheduleAtFixedRate(() -> {
            try {
                Map<UUID, ScoreboardSign> map = new HashMap<>(scoreboardMap);
                map.forEach((uuid, sb) -> {
                    sb.update();
                });
                map.clear();
                map = null;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 50 * 20, 50, TimeUnit.MILLISECONDS);
    }
}