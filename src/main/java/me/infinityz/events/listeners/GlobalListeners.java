package me.infinityz.events.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.infinityz.UHC;

/**
 * GlobalListeners
 */
public class GlobalListeners extends SkeletonListener {

    public GlobalListeners(UHC instance) {
        super(instance);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType().equals(Material.STONE)) {
            e.getBlock().setType(Material.AIR);
            e.getBlock().getLocation().getWorld()
                    .dropItem(new Location(e.getBlock().getWorld(), e.getBlock().getX() + 0.5,
                            e.getBlock().getY() + 0.2, e.getBlock().getZ() + 0.5), new ItemStack(Material.COBBLESTONE))
                    .setVelocity(new Vector(0.0, 0.2, 0.0));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        final TeleportCause portalType = event.getCause();
        if ((event.getTo() != null && event.getTo().getWorld().getEnvironment() == Environment.THE_END)
                || portalType == TeleportCause.END_PORTAL) {
            // Handle end in the future?
        } else {
            processNetherPortalEvent(event);
        }
    }

    private void processNetherPortalEvent(final PlayerPortalEvent event) {
        final Player player = event.getPlayer();
        final World fromWorld = player.getWorld();
        World toWorld;
        final Environment oldEnvironment;
        boolean useDimension = true;

        oldEnvironment = player.getWorld().getEnvironment() == Environment.NETHER ? Environment.NETHER
                : Environment.NORMAL;
        final String toWorldName;
        if (oldEnvironment == Environment.NETHER) {
            toWorldName = player.getWorld().getName().replaceFirst("_nether$", "");
        } else {
            toWorldName = player.getWorld().getName().concat("_nether");
        }
        toWorld = Bukkit.getWorld(toWorldName);
        if (toWorld == null) {
            player.sendMessage("There isn't a world for us to teleport you to.");
            return;
        }
        if (toWorld.getEnvironment().equals(fromWorld.getEnvironment()))
            useDimension = false;
        TravelAgent a = event.getPortalTravelAgent();

        Bukkit.broadcastMessage("RUNS>??");
        final double blockRatio = useDimension ? (oldEnvironment == Environment.NETHER ? 8 : 0.125) : 1;

        final Location fromLocation = new Location(fromWorld, player.getLocation().getX(), player.getLocation().getY(),
                player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        final Location toLocation = checkOutside(new Location(toWorld, (player.getLocation().getX() * blockRatio),
                player.getLocation().getY(), (player.getLocation().getZ() * blockRatio), player.getLocation().getYaw(),
                player.getLocation().getPitch()), 3000);

        List<Block> blocks = findPortalBlocks(toLocation);

        blocks.forEach(block -> {
            for (BlockFace face : BlockFace.values()) {
                Block gb = block.getRelative(face);
                if (gb.getType() != Material.OBSIDIAN && gb.getType() != Material.PORTAL
                        && gb.getType() != Material.AIR) {
                    gb.setType(Material.AIR);
                }
            }
        });
        event.setTo(toLocation);
        event.setFrom(fromLocation);
        event.useTravelAgent(true);
    }

    @EventHandler
    public void e(PortalCreateEvent e) {
        Bukkit.broadcastMessage("test");

    }

    List<Block> findPortalBlocks(Location loc) {
        return findPortalBlocks(loc.getWorld().getBlockAt(loc));
    }

    List<Block> findPortalBlocks(Block loc) {
        List<Block> lBlocks = new ArrayList<>();
        int radius = 15;
        for (int x = loc.getX() - radius; x <= loc.getX() + radius; x++) {
            for (int y = loc.getY() - radius; y <= loc.getY() + radius; y++) {
                for (int z = loc.getZ() - radius; z <= loc.getZ() + radius; z++) {
                    Block b = loc.getWorld().getBlockAt(x, y, z);
                    if (b.getType() == Material.OBSIDIAN || b.getType() == Material.PORTAL) {
                        lBlocks.add(b);
                    }
                }
            }
        }

        return lBlocks;
    }

    // Make sure no portals are being generated outside the world border.
    // Use a simple integer check no ensure it.
    int checkOutside(int number, int max) {
        if (number >= max && number > 0)
            return max;
        else if ((number < 0 && number <= (-max)))
            return -max;
        return number;
    }

    // Check a location with a function that takes a location object, instead of
    // checking each number manually.
    Location checkOutside(Location loc, int max) {
        loc.setX(checkOutside(loc.getBlockX(), max));
        loc.setZ(checkOutside(loc.getBlockZ(), max));
        return loc;
    }

    @EventHandler
    public void portalCreateEvent(
            PortalCreateEvent e) {/*
                                   * e.getBlocks().forEach(at -> {
                                   * at.getRelative(BlockFace.EAST).setType(Material.GLOWSTONE); });
                                   */

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        instance.scoreboardManager.scoreboardMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void w(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeather(ThunderChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        instance.scoreboardManager.scoreboardMap.remove(e.getPlayer().getUniqueId());
    }

}