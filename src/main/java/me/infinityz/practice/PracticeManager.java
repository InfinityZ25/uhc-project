package me.infinityz.practice;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;

import me.infinityz.UHC;

/**
 * PracticeManager
 */
public class PracticeManager {

    private UHC instance;
    public World practice_world;
    public boolean enabled;
    public int teleport_radius;

    public PracticeManager(UHC instance) {
        this.instance = instance;
        this.enabled = false;
        this.practice_world = instance.getServer()
                .createWorld(new WorldCreator("Practice").type(WorldType.FLAT).generateStructures(false));
        this.practice_world.setGameRuleValue("doMobSpawning", "false");
        this.practice_world.setGameRuleValue("doFireTick", "false");
        this.practice_world.setGameRuleValue("doDaylightCycle", "false");
        this.teleport_radius = 100;
        // Move this later
        uhcWorld();
    }

    public void uhcWorld() {
        WorldCreator wc = new WorldCreator("UHC");
        wc.environment(Environment.NORMAL);
        wc.createWorld();
        WorldCreator NETHER = new WorldCreator("UHC_nether");
        NETHER.environment(Environment.NETHER);
        NETHER.createWorld();
    }

    public Location getLocation() {
        Location loc = new Location(practice_world, 0, 0, 0);
        loc.setX(loc.getX() + Math.random() * teleport_radius * 2.0 - teleport_radius);
        loc.setZ(loc.getZ() + Math.random() * teleport_radius * 2.0 - teleport_radius);
        return loc.getWorld().getHighestBlockAt(loc).getLocation().add(0.0, 2.0, 0.0);
    }

}