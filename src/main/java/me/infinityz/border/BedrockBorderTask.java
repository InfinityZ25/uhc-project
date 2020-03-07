package me.infinityz.border;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Border
 */
public class BedrockBorderTask extends BukkitRunnable {
    // Take variables from the instance method.
    World world;
    int size, height, maxtick, northInt, southInt, westInt, eastInt;
    // Keep track of what loops have been completed
    boolean north, south, west, east = false;
    // Always know if you are ticking out of time, thus causing lag.
    long milliseconds;
    // Initial time
    long start;

    public BedrockBorderTask(World world, int size, int height, int maxtick) {
        this.world = world;
        this.size = size;
        this.height = height;
        this.northInt = 0;
        this.southInt = 1;
        this.westInt = 1;
        this.eastInt = 1;
        this.maxtick = maxtick;
        this.start = System.currentTimeMillis();
    }

    @Override
    public void run() {
        // Set Milliseconds to current time to keep track of lag
        milliseconds = System.currentTimeMillis();
        // Go one by one making sure that their part of the wall has been built.
        if (!north) {
            for (; northInt <= size * 2; northInt++) {
                if (milliseconds + maxtick <= System.currentTimeMillis()) {
                    break;
                }
                int highest = world.getHighestBlockYAt(size - northInt, size);
                for (int j = height; j > -1; j--) {
                    world.getBlockAt(size - northInt, highest + j, size).setType(Material.BEDROCK);
                }
            }
            // Confirm that the loop is done and readt to move to the next side of the wall.
            if (northInt > (size * 2)) {
                north = true;
            } else {
                return;
            }
        }
        if (!south) {
            for (; southInt <= size * 2; southInt++) {
                if (milliseconds + maxtick <= System.currentTimeMillis()) {
                    break;
                }
                int highest = world.getHighestBlockYAt(size, size - southInt);
                for (int j = height; j > -1; j--) {
                    world.getBlockAt(size, highest, size - southInt).setType(Material.BEDROCK);
                }
            }

            if (southInt > (size * 2)) {
                south = true;
            } else {
                return;
            }
        }
        if (!west) {
            for (; westInt <= size * 2; westInt++) {
                if (milliseconds + maxtick <= System.currentTimeMillis()) {
                    break;
                }
                int highest = world.getHighestBlockYAt(size - westInt, -size);
                for (int j = height; j > -1; j--) {
                    world.getBlockAt(size - westInt, highest, -size).setType(Material.BEDROCK);
                }
            }

            if (westInt > (size * 2)) {
                west = true;
            } else {
                return;
            }
        }
        if (!east) {
            for (; eastInt <= size * 2; eastInt++) {
                if (milliseconds + maxtick <= System.currentTimeMillis()) {
                    break;
                }
                if (eastInt == size * 2) {
                    continue;
                }
                int highest = world.getHighestBlockYAt(-size, eastInt - size);
                for (int j = height; j > -1; j--) {
                    world.getBlockAt(-size, highest, eastInt - size).setType(Material.BEDROCK);
                }
            }

            if (eastInt > (size * 2)) {
                east = true;
            } else {
                return;
            }
        }
        this.cancel();
    }
}