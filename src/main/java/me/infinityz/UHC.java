package me.infinityz;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.infinityz.combatlogger.SkeletonCombatLogger;
import me.infinityz.commands.CommandManager;
import me.infinityz.events.ListenerManager;
import me.infinityz.scatter.Scatter;
import me.infinityz.scoreboard.ScoreboardManager;
import net.minecraft.server.v1_8_R3.EntitySkeleton;

/**
 * UHC
 */
public class UHC extends JavaPlugin implements Listener {

    private static UHC instance;
    public ScoreboardManager scoreboardManager;
    public ListenerManager listenerManager;
    public CommandManager commandManager;
    public Scatter scatter;
    public SkeletonCombatLogger skeleton;

    public List<Location> locations;

    public static UHC getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.scoreboardManager = new ScoreboardManager();
        this.listenerManager = new ListenerManager(this);
        this.commandManager = new CommandManager(this);
        this.scatter = new Scatter(this);

        skeleton = new SkeletonCombatLogger(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle());
        skeleton.registerEntity("CombatLogger", 51, EntitySkeleton.class, SkeletonCombatLogger.class);
    }

    @Override
    public void onDisable() {

    }

}