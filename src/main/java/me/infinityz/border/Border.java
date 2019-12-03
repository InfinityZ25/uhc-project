package me.infinityz.border;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Border
 */
public class Border extends BukkitRunnable{
    //Take variables from the instance method.
    World world;
    int size, height, maxtick, northInt, southInt, westInt, eastInt;
    //Keep track of what loops have been completed
    boolean north, south, west, east = false;
    //Always know if you are ticking out of time, thus causing lag.
    long milliseconds;
    //Initial time
    long start;

    public Border(World world, int size, int height, int maxtick){
        this.world = world;
        this.size = size;
        this.height = height;
        this.northInt = 1;
        this.southInt = 0;
        this.westInt = 0;
        this.eastInt = 0;
        this.maxtick = maxtick;
        this.start = System.currentTimeMillis();
    }
    
    void putWall(World world, int size, int wall_size){

        long start = System.currentTimeMillis();   

        Bukkit.broadcastMessage("Starting to build the border!");
        for (int i = 1; i <= size*2; i++) {
            for (int j = world.getHighestBlockYAt(size-i, size) + wall_size; j > 0; j--) {
                world.getBlockAt(size-i, j, size).setType(Material.BEDROCK);                
            }            
        }

        for (int i = 0; i <= size*2; i++) {
            for (int j = world.getHighestBlockYAt(size, size-i) + wall_size; j > 0; j--) {
            world.getBlockAt(size, j, size-i).setType(Material.BEDROCK);
            }
        }

        for (int i = 0; i <= (size*2)+1; i++) {
            for (int j = world.getHighestBlockYAt(size-i, -size-1) + wall_size; j > 0; j--) {
            world.getBlockAt(size-i, j, -size-1).setType(Material.BEDROCK);
            }
        }

        for (int i = 0; i <= size*2; i++) {
            for (int j = world.getHighestBlockYAt(-size-1, size-i) + wall_size; j > 0; j--) {
            world.getBlockAt(-size-1, j, size-i).setType(Material.BEDROCK);
            }
        }
        long end = System.currentTimeMillis();
        Bukkit.broadcastMessage("It took "  + (end-start) + "ms!");
    }

    //TO-DO: Consider reducing the complexity to two for loops. One north-east, and one south-west. It would considerably reduce the time it takes to execute
    @Override
    public void run() {
        //Set Milliseconds to current time to keep track of lag
        milliseconds = System.currentTimeMillis();
        //Go one by one making sure that their part of the wall has been built.
        if(!north){
            for (; northInt <= size*2; northInt++) {
                if(milliseconds +maxtick <= System.currentTimeMillis()){
                    break;
                }
                for (int j = world.getHighestBlockYAt(size-northInt, size) + height; j > 0; j--) {
                    world.getBlockAt(size-northInt, j, size).setType(Material.BEDROCK);                
                }                     
            }
            //Confirm that the loop is done and readt to move to the next side of the wall.
            if(northInt >(size*2)){
                north=true;
                Bukkit.broadcastMessage("out");
            }
            return;
        }
        if(!south){
            for (; southInt <= size*2; southInt++) {
                if(milliseconds +maxtick <= System.currentTimeMillis()){
                    break;
                }
                for (int j = world.getHighestBlockYAt(size, size-southInt) + height; j > 0; j--) {
                    world.getBlockAt(size, j, size-southInt).setType(Material.BEDROCK);                
                }                     
            }
            
            if(southInt >(size*2)){
                south=true;
                Bukkit.broadcastMessage("out2");
            }
            return;
        }
        if(!west){
            for (; westInt <= (size*2)+1; westInt++) {
                if(milliseconds +maxtick <= System.currentTimeMillis()){
                    break;
                }
                for (int j = world.getHighestBlockYAt(size-westInt, -size-westInt) + height; j > 0; j--) {
                world.getBlockAt(size-westInt, j, -size-1).setType(Material.BEDROCK);
                }
            }

            if(westInt >(size*2)+1){
                west=true;
                Bukkit.broadcastMessage("out3");
            }            
            return;
        }
        if(!east){            
            for (; eastInt <= size*2; eastInt++) {
                if(milliseconds +maxtick <= System.currentTimeMillis()){
                    break;
                }
                for (int j = world.getHighestBlockYAt(-size-1, size-eastInt) + height; j > 0; j--) {
                world.getBlockAt(-size-1, j, size-eastInt).setType(Material.BEDROCK);
                }
            }
            
            if(eastInt >(size*2)){
                east=true;
                Bukkit.broadcastMessage("out4");
            }
            return;
        }
        Bukkit.broadcastMessage("It took " + (System.currentTimeMillis() - start) + "ms to build a " + size + "x" + size + " border!");
        this.cancel();
    }
}