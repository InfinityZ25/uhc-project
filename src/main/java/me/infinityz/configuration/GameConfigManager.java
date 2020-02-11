package me.infinityz.configuration;

import org.bukkit.Bukkit;

import me.infinityz.UHC;

/**
 * GameConfigManager
 */
public class GameConfigManager {

    public GameConfig gameConfig;
    public GameConfigListener gameConfigListener;

    public GameConfigManager(){
        gameConfig = new GameConfig();
        gameConfigListener = new GameConfigListener();
        Bukkit.getPluginManager().registerEvents(gameConfigListener, UHC.getInstance());
    }

}