package me.infinityz.scatter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.UHC;

/**
 * TeleportTask
 */
public class TeleportTask extends BukkitRunnable{

    private UHC instance;
    private List<Player> players;
    long start_time;

    public TeleportTask(UHC instance){
        this.instance = instance;
        this.players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player ->{
            this.players.add(player);
        });
        Bukkit.broadcastMessage("Teleport task has been called!");
        this.start_time = System.currentTimeMillis();

    }

    @Override
    public void run() {
        //double tps = MinecraftServer.getServer().recentTps[0];
        long milliseconds = System.currentTimeMillis();/*
        if(tps < 18){        
            Bukkit.broadcastMessage("Server tps is at " + tps + ", it has to be at least 19 TPS to start the telportation.");
            return;
        }*/
        for (int i = this.players.size(); i > 0; i--) {
            
            if(milliseconds + 100 <= System.currentTimeMillis()){        
                Bukkit.broadcastMessage("Halting teleportation until stable tps");
                break;
            }
                players.get(i-1).teleport(instance.locations.get(0));
                //Bukkit.broadcastMessage(players.get(i-1).getName() + " has been teleported!");
                players.remove(i-1);
                instance.locations.remove(0);
            
        }
        if(players.isEmpty()){
            Bukkit.broadcastMessage("Teleportation is done!\n It took " + ((System.currentTimeMillis() - this.start_time)/1000D) + "s");
            this.cancel();

        }


    }

    
}