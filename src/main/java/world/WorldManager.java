package world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

import me.infinityz.UHC;

/**
 * WorldManager
 */
public class WorldManager {

    UHC instance;

    public WorldManager(UHC instance) {
        this.instance = instance;
        this.createWorld("UHC", Environment.NORMAL, true);
        this.checkMainWorlds();
    }

    void createWorld(String worldName, Environment environment, boolean nether) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.environment(environment);
        worldCreator.createWorld();
        if (nether) {
            WorldCreator netherWorldCreator = new WorldCreator(worldName + "_nether");
            netherWorldCreator.environment(environment);
            netherWorldCreator.createWorld();
        }
    }

    void checkMainWorlds() {
        World nether = Bukkit.getWorld(Bukkit.getWorlds().get(0).getName() + "_nether");
        if (nether != null)
            Bukkit.unloadWorld(nether, false);
        World end = Bukkit.getWorld(Bukkit.getWorlds().get(0).getName() + "_the_end");
        if (end != null)
            Bukkit.unloadWorld(nether, false);

    }
}