package me.infinityz.events.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.infinityz.UHC;
import me.infinityz.scoreboard.UHCBoard;

/**
 * GlobalListeners
 */
public class GlobalListeners extends SkeletonListener {

    public GlobalListeners(UHC instance) {
        super(instance);
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(e.getBlock().getType().equals(Material.STONE)){
            e.getBlock().setType(Material.AIR);
            e.getBlock().getLocation().getWorld().dropItem(new Location(e.getBlock().getWorld(), e.getBlock().getX()+0.5, e.getBlock().getY()+0.2, e.getBlock().getZ()+0.5), new ItemStack(Material.COBBLESTONE)).setVelocity(new Vector(0.0,0.2,0.0));
        }
    }
   
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        UHCBoard board = new UHCBoard(player, "  &4&lUHC SCOREBOARD  ",
                                    "Game line",
                                    " ",
                                    "Other line",
                                    "  ",
                                    "Yet another lineeeeeeeeeee");
        board.toString();
        player.sendMessage(String.format("Hello and welcome %1$s.\nThe current time in milliseconds is %2$s!", player.getName(), System.currentTimeMillis()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        instance.scoreboardManager.scoreboardMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void w(WeatherChangeEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    public void onWeather(ThunderChangeEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e){
        instance.scoreboardManager.scoreboardMap.remove(e.getPlayer().getUniqueId());
    }



    
}