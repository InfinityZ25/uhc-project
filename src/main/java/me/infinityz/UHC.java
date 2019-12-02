package me.infinityz;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.infinityz.commands.CommandManager;
import me.infinityz.events.ListenerManager;
import me.infinityz.scatter.Scatter;
import me.infinityz.scoreboard.ScoreboardManager;

/**
 * UHC
 */
public class UHC extends JavaPlugin implements Listener {

    private static UHC instance;
    public ScoreboardManager scoreboardManager;
    public ListenerManager listenerManager;
    public CommandManager commandManager;
    public Scatter scatter;

    public static UHC getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.scoreboardManager = new ScoreboardManager();
        this.listenerManager = new ListenerManager(this);
        this.commandManager = new CommandManager(this);
        this.scatter = new Scatter(this);
    }

    @Override
    public void onDisable() {

    }

    
}