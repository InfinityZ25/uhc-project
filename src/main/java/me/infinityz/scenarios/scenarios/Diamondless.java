package me.infinityz.scenarios.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.infinityz.scenarios.IScenario;

/**
 * GoldLess
 */
public class Diamondless extends IScenario {

    public Diamondless() {
        this.description = "You can't mine diamonds.\nWhen a player dies, they drop 3 diamonds!";
    }

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

    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(PlayerDeathEvent e) {
        e.getDrops().add(new ItemStack(Material.DIAMOND, 3));
    }

}