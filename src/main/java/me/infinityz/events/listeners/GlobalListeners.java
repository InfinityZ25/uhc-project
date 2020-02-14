package me.infinityz.events.listeners;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.infinityz.UHC;
import me.infinityz.protocol.Reflection;
import me.infinityz.scenarios.events.ScenarioDisabledEvent;
import me.infinityz.scenarios.events.ScenarioEnabledEvent;

/**
 * GlobalListeners
 */
public class GlobalListeners extends SkeletonListener {

    // Border maybe
    Map<UUID, Collection<Vector>> map = new HashMap<>();

    public GlobalListeners(final UHC instance) {
        super(instance);
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
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

    @EventHandler
    public void scenEnable(ScenarioEnabledEvent e) {
        Bukkit.broadcastMessage(e.getScenario().getClass().getSimpleName() + "has been enabled");
    }

    @EventHandler
    public void scenDisable(ScenarioDisabledEvent e) {
        Bukkit.broadcastMessage(e.getScenario().getClass().getSimpleName() + "has been disabled");
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

        Bukkit.broadcastMessage("RUNS>??");
        final double blockRatio = useDimension ? (oldEnvironment == Environment.NETHER ? 8 : 0.125) : 1;

        final Location fromLocation = new Location(fromWorld, player.getLocation().getX(), player.getLocation().getY(),
                player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        final Location toLocation = checkOutside(new Location(toWorld, (player.getLocation().getX() * blockRatio),
                player.getLocation().getY(), (player.getLocation().getZ() * blockRatio), player.getLocation().getYaw(),
                player.getLocation().getPitch()), 3000);

        final List<Block> blocks = findPortalBlocks(toLocation);

        blocks.forEach(block -> {
            for (final BlockFace face : BlockFace.values()) {
                final Block gb = block.getRelative(face);
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
    public void e(final PortalCreateEvent e) {
        Bukkit.broadcastMessage("test");

    }

    List<Block> findPortalBlocks(final Location loc) {
        return findPortalBlocks(loc.getWorld().getBlockAt(loc));
    }

    List<Block> findPortalBlocks(final Block loc) {
        final List<Block> lBlocks = new ArrayList<>();
        final int radius = 15;
        for (int x = loc.getX() - radius; x <= loc.getX() + radius; x++) {
            for (int y = loc.getY() - radius; y <= loc.getY() + radius; y++) {
                for (int z = loc.getZ() - radius; z <= loc.getZ() + radius; z++) {
                    final Block b = loc.getWorld().getBlockAt(x, y, z);
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
    int checkOutside(final int number, final int max) {
        if (number >= max && number > 0)
            return max;
        else if ((number < 0 && number <= (-max)))
            return -max;
        return number;
    }

    // Check a location with a function that takes a location object, instead of
    // checking each number manually.
    Location checkOutside(final Location loc, final int max) {
        loc.setX(checkOutside(loc.getBlockX(), max));
        loc.setZ(checkOutside(loc.getBlockZ(), max));
        return loc;
    }

    @EventHandler
    public void portalCreateEvent(
            final PortalCreateEvent e) {/*
                                         * e.getBlocks().forEach(at -> {
                                         * at.getRelative(BlockFace.EAST).setType(Material.GLOWSTONE); });
                                         */

    }

    // Handle the old-style enchanting table starts.
    @EventHandler
    public void inventoryClickEvent(final InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof EnchantingInventory) {
            if (e.getSlot() == 1) {
                try {

                    final Class<?> anvilClass = Reflection.getClass("{nms}.ContainerEnchantTable");

                    final Field field = anvilClass.getField("use_lapis");
                    final boolean bol = field.getBoolean(anvilClass);
                    if (!bol)
                        e.setCancelled(true);

                } catch (final Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        e.setJoinMessage("");
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        instance.scoreboardManager.scoreboardMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void w(final WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeather(final ThunderChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDisconnect(final PlayerQuitEvent e) {
        instance.scoreboardManager.scoreboardMap.remove(e.getPlayer().getUniqueId());
    }

}