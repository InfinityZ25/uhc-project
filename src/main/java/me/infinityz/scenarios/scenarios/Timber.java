package me.infinityz.scenarios.scenarios;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.infinityz.UHC;
import me.infinityz.scenarios.IScenario;

/**
 * Timber
 */
public class Timber extends IScenario {

    // Method that ensures ores don't fly like in many other servers
    void dropCenter(ItemStack itemStack, Location location) {
        Location centeredLocation = new Location(location.getWorld(), location.getBlockX() + 0.5,
                location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
        Item item = location.getWorld().dropItem(centeredLocation, itemStack);
        item.setVelocity(new Vector(0.0, 0.2, 0.0));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (event.getBlock().getType() == Material.LOG || event.getBlock().getType() == Material.LOG_2) {
            event.setCancelled(true);
            final List<Block> l = new ArrayList<>();
            l.add(event.getBlock());

            Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
                Block b_up = block.getRelative(BlockFace.UP);
                if (b_up.getType().toString().toLowerCase().contains("log")) {
                    while (b_up.getType().toString().toLowerCase().contains("log")) {
                        l.add(b_up);
                        b_up = b_up.getRelative(BlockFace.UP);
                    }
                }
                Block b_down = block.getRelative(BlockFace.DOWN);
                if (b_down.getType().toString().toLowerCase().contains("log")) {
                    while (b_down.getType().toString().toLowerCase().contains("log")) {
                        l.add(b_down);
                        b_down = b_down.getRelative(BlockFace.DOWN);
                    }
                }
                Bukkit.getScheduler().runTask(UHC.getInstance(), () -> {
                    dropCenter(new ItemStack(l.get(0).getType(), l.size(), l.get(0).getData()), block.getLocation());
                    l.forEach(all -> {
                        all.setType(Material.AIR);
                    });
                    l.clear();
                });

            });
        }
    }

}