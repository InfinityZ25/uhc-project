package me.infinityz.configuration;

import java.util.UUID;

import org.bukkit.Bukkit;

import me.infinityz.UHC;

/**
 * GameConfigManager
 */
public class GameConfigManager {

    public GameConfig gameConfig;
    public GameConfigListener gameConfigListener;
    public String last_known_host_name;
    public UUID host;

    public GameConfigManager(UHC instance) {
        gameConfig = new GameConfig();
        gameConfigListener = new GameConfigListener(this);
        last_known_host_name = "";
        Bukkit.getPluginManager().registerEvents(gameConfigListener, UHC.getInstance());
        GameConfigCMD configCMD = new GameConfigCMD(this);
        instance.getCommand("config").setExecutor(configCMD);
        instance.getCommand("config").setTabCompleter(configCMD);

    }

}