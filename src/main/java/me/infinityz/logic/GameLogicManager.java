package me.infinityz.logic;

import me.infinityz.UHC;

/**
 * GameLogic
 */
public class GameLogicManager {
    public int game_time;
    public boolean game_pvp, finished;
    public UHC instance;
    public GameLogicTask gameLogicTask;

    public GameLogicManager(UHC instance) {
        this.instance = instance;
        this.finished = false;
        this.game_pvp = false;
        this.gameLogicTask = new GameLogicTask(this);
    }
}