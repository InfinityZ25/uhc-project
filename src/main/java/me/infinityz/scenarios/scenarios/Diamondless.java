package me.infinityz.scenarios.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import me.infinityz.scenarios.IScenario;

/**
 * GoldLess
 */
public class Diamondless extends IScenario {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.DIAMOND_ORE) {
            e.getBlock().setType(Material.AIR);
            ((ExperienceOrb) e.getBlock().getWorld().spawn(e.getBlock().getLocation(), ExperienceOrb.class))
                    .setExperience(e.getExpToDrop());
            e.setCancelled(true);
            e.getPlayer().updateInventory();
            e.getPlayer().sendMessage("You cannot mine diamond on diamondless!");
        }

    }

}