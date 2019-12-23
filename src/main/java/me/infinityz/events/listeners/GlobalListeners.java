package me.infinityz.events.listeners;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
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

        final double blockRatio = useDimension ? (oldEnvironment == Environment.NETHER ? 8 : 0.125) : 1;

        final Location fromLocation = new Location(fromWorld, player.getLocation().getX(), player.getLocation().getY(),
                player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        final Location toLocation = new Location(toWorld, (player.getLocation().getX() * blockRatio),
                player.getLocation().getY(), (player.getLocation().getZ() * blockRatio), player.getLocation().getYaw(),
                player.getLocation().getPitch());
        event.setTo(toLocation);
        event.setFrom(fromLocation);
        event.useTravelAgent(true);
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