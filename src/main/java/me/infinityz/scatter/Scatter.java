package me.infinityz.scatter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.UHC;

/**
 * Scatter
 */
public class Scatter extends BukkitRunnable{

    private UHC instance;
    public List<Location> locations;

    public Scatter(UHC instance){
        this.instance = instance;
        this.locations = new ArrayList<Location>();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    Location findScatterLocation(World world, Integer radius){
        Location loc = new Location(world, 0, 0,0);  
        //Use Math#Random to obtain a random integer that can be used as a location.      
        loc.setX(loc.getX() + Math.random() * radius * 2.0 - radius);
        loc.setZ(loc.getZ() + Math.random() * radius * 2.0 - radius);
        loc = loc.getWorld().getHighestBlockAt(loc).getLocation();
        //Check if block is liquid, and above sea level, is it's bellow sea level it can assumed that player spawned in a cave.
        if (loc.getBlockY() < 60 || loc.getBlock().isLiquid() || loc.getBlock().getRelative(BlockFace.DOWN).isLiquid()|| loc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isLiquid()|| loc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isLiquid() || !loc.getBlock().isEmpty()) {
            //Since our critaria wasn't met, we'll have to recursively look for another location.
            findScatterLocation(world, radius);
        }
        //A location object is returned once we reach this step, next step is to validate the location from others.
        return loc;
    }
    //Private method that validates the location in relation to the other locations
    boolean validateLocation(Location location, int distance_limit){
        if(locations.isEmpty()) return true;
        //Loop through all the other locations and make sure the distance limit is enough.
        for(Location loc : locations) if(location.distance(loc)<= distance_limit)return false;
        //If previous checks weren't met then this location must be valid.
        return true;
    }
    //This is the method that will be access from outside the class, hence it's public
    public Location findValidLocation(World world, int radius, int distance_limit){
        Location loc = findScatterLocation(world, radius);
        //Assert that the location found is valid and don't stop looping until found.
        if(!validateLocation(loc, distance_limit)) findValidLocation(world, radius, distance_limit);
        //If this point is reached, it means the location returned is valid.
        return loc;
    }

    public void findLotsOfLocation(World world, int radius, int distance_limit, int ammount){
        for (int i = 0; i < ammount; i++) {
            Location loc = findValidLocation(world, radius, distance_limit);
            if(!isSafe(loc)){
                i--;
                continue;
            }
            locations.add(centerLocation(loc));
        }
        System.out.println("Locations found");
    }

    //Recursive method to avoid players spawning on walls.
    private Location centerLocation(Location loc){
        return new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY() + 0.5, loc.getBlockZ() + 0.5);
    }
    private boolean isSafe(Location loc){
        if(loc.getBlock().isLiquid() || loc.getBlock().getRelative(BlockFace.DOWN).isLiquid()|| loc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isLiquid())return false;
        return true;
    }

    
}