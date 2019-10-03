package me.infinityz.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * ScoreboardManager
 */
public class ScoreboardManager {
    public Map<UUID, ScoreboardSign> scoreboardMap;

    public ScoreboardManager() {
        scoreboardMap = new HashMap<>();
    }
}