package me.infinityz.scenarios.scenarios;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import me.infinityz.scenarios.IScenario;

/**
 * Timber
 */
public class Timber extends IScenario {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (event.getBlock().getType() == Material.LOG || event.getBlock().getType() == Material.LOG_2) {
            event.setCancelled(true);
            Location loc = block.getLocation();
            loc.getBlock().breakNaturally();
            loc.getBlock().setType(Material.AIR);
            for (int i = 0; i < loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockY()); i++) {
                Location location = loc.add(0.0D, 1.0D, 0.0D);
                if (location.getBlock().getType() == Material.LOG || location.getBlock().getType() == Material.LOG_2) {
                    location.getBlock().breakNaturally();
                    location.getBlock().setType(Material.AIR);
                }
            }
        }
    }

}