package me.infinityz.configuration;

import org.bukkit.Bukkit;

import me.infinityz.UHC;

/**
 * GameConfigManager
 */
public class GameConfigManager {

    public GameConfig gameConfig;
    public GameConfigListener gameConfigListener;

    public GameConfigManager(UHC instance){
        gameConfig = new GameConfig();
        gameConfigListener = new GameConfigListener(this);
        Bukkit.getPluginManager().registerEvents(gameConfigListener, UHC.getInstance());
        GameConfigCMD configCMD = new GameConfigCMD(this);
        instance.getCommand("config").setExecutor(configCMD);
        instance.getCommand("config").setTabCompleter(configCMD);
        
    }

}