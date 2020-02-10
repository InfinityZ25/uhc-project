package me.infinityz.scenarios.scenarios;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.infinityz.scenarios.IScenario;

/**
 * GHeads
 */
public class GHeads extends IScenario{

    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(PlayerDeathEvent e){
        e.getDrops().add(new ItemStack(Material.DIAMOND, 64));
        //Obviously not done. But the principle works with timebomb no problem. Just implement the rest of the code

    }

    
}