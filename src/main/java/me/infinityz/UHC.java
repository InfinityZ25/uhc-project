package me.infinityz;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.infinityz.combatlogger.CombatLoggerManager;
import me.infinityz.combatlogger.SkeletonCombatLogger;
import me.infinityz.commands.CommandManager;
import me.infinityz.configuration.GameConfigManager;
import me.infinityz.events.ListenerManager;
import me.infinityz.practice.PracticeManager;
import me.infinityz.protocol.ProtocolManager;
import me.infinityz.scatter.Scatter;
import me.infinityz.scenarios.ScenariosManager;
import me.infinityz.scoreboard.ScoreboardManager;
import me.infinityz.teams.TeamManager;
import me.infinityz.whitelist.WhitelistManager;
import me.infinityz.world.WorldManager;

/**
 * UHC
 */
public class UHC extends JavaPlugin implements Listener {

    private static UHC instance;
    public ScoreboardManager scoreboardManager;
    public ListenerManager listenerManager;
    public CommandManager commandManager;
    public PracticeManager practiceManager;
    public CombatLoggerManager combatLoggerManager;
    public WorldManager worldManager;
    public Scatter scatter;
    public SkeletonCombatLogger skeleton;
    public ProtocolManager protocolManager;
    public WhitelistManager whitelistManager;
    public TeamManager teamManager;
    public ScenariosManager scenariosManager;
    public GameConfigManager gameConfigManager;
    // Deprecated, move this out of here
    public List<Location> locations;

    public static UHC getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        GameStage.stage = GameStage.LOADING;
        this.scoreboardManager = new ScoreboardManager();
        this.whitelistManager = new WhitelistManager(this);
        this.teamManager = new TeamManager(this);
        this.practiceManager = new PracticeManager(this);
        this.listenerManager = new ListenerManager(this);
        this.commandManager = new CommandManager(this);
        this.combatLoggerManager = new CombatLoggerManager(this);
        this.worldManager = new WorldManager(this);
        this.scatter = new Scatter(this);
        this.protocolManager = new ProtocolManager(this);
        this.scenariosManager = new ScenariosManager();
        this.gameConfigManager = new GameConfigManager(this);
        GameStage.stage = GameStage.LOBBY;
    }

    @Override
    public void onDisable() {

    }

    public void deleteGameWorlds() {
        Bukkit.getOnlinePlayers().forEach(all -> {
            all.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        });
        File file = new File("UHC");
        File file2 = new File("UHC_nether");
        deleteDirectory(file);
        deleteDirectory(file2);
    }

    public void deleteDirectory(File file) {
        try {
            Bukkit.unloadWorld(file.getName(), false);
            FileUtils.deleteDirectory(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum GameStage {
        LOADING, LOBBY, PRE_GAME, SCATTERING, IN_GAME, DEATHMATCH, DONE;

        public static GameStage stage;
    }

}