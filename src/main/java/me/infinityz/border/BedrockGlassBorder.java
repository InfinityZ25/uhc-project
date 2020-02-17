package me.infinityz.border;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.infinityz.UHC;

/**
 * BedrockGlassBorder
 */

@SuppressWarnings("all")
public class BedrockGlassBorder extends BukkitRunnable {

    Map<UUID, Collection<Vector>> map;
    UHC instance;

    public BedrockGlassBorder(UHC instance) {
        this.map = new HashMap<>();
        this.instance = instance;
    }

    @Override
    public void run() {
        // No comments on the task. If you don't understand what the code does, you
        // shouldn't touch it.
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (isNearWall(player, instance.gameConfigManager.gameConfig.map_size - 3, 7)) {
                List<Vector> collection = getLocationalVectors(player.getLocation(), 2, 3, 7,
                        instance.gameConfigManager.gameConfig.map_size);

                if (map.containsKey(player.getUniqueId())) {
                    List<Vector> vList = new ArrayList<>(map.get(player.getUniqueId()));
                    vList.parallelStream().forEach(vector -> {
                        if (collection.contains(vector))
                            return;
                        int block_id = vector.toLocation(player.getWorld()).getWorld()
                                .getBlockTypeIdAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
                        if (block_id != 0) {
                            player.sendBlockChange(vector.toLocation(player.getWorld()), Material.AIR, (byte) 0);
                        }

                    });
                    vList = null;
                }
                collection.forEach(vector -> player.sendBlockChange(vector.toLocation(player.getLocation().getWorld()),
                        Material.STAINED_GLASS, (byte) 14));
                map.put(player.getUniqueId(), collection);
                return;
            }
            if (map.containsKey(player.getUniqueId())) {
                List<Vector> vectors = new ArrayList<>(map.get(player.getUniqueId()));
                vectors.forEach(vector -> {
                    int block_id = vector.toLocation(player.getWorld()).getWorld().getBlockTypeIdAt(vector.getBlockX(),
                            vector.getBlockY(), vector.getBlockZ());
                    if (block_id != 0) {
                        player.sendBlockChange(vector.toLocation(player.getWorld()), Material.AIR, (byte) 0);
                    }
                });
                vectors = null;
                map.remove(player.getUniqueId());
            }
        });

    }

    // Method that checks weather the player is near the wall or not
    boolean isNearWall(final Player p, final double wall, final double check_distance) {
        final Location loc = p.getLocation();
        // Get the absolute int of player's location and check if it is in proximity to
        // a "wall"
        if (Math.abs(loc.getX()) + check_distance >= wall || Math.abs(loc.getZ()) + check_distance >= wall)
            return true;
        // Return the inverse.
        return false;
    }

    // Obtains the right blocks near to the player that must be set to crystal!
    List<Vector> getLocationalVectors(Location loc, int min_height, int max_height, int radius, int wall) {
        List<Vector> vector_list = new ArrayList<>();
        for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
            for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                int absolute_x = Math.abs(x);
                int absolute_z = Math.abs(z);
                if (absolute_x != wall && absolute_z != wall)
                    continue;
                if (absolute_x > wall || absolute_z > wall)
                    continue;
                for (int y = loc.getBlockY() - min_height; y <= loc.getBlockY() + max_height; y++) {
                    int block_id = loc.getWorld().getBlockTypeIdAt(x, y, z);
                    if (block_id != 0)
                        continue;
                    vector_list.add(new Vector(x, y, z));
                }
            }
        }
        return vector_list;
    }

}