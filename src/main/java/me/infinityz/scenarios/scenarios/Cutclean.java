package me.infinityz.scenarios.scenarios;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.infinityz.scenarios.IScenario;

/**
 * Cutclean
 */
public class Cutclean extends IScenario{

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        if(e.isCancelled())return;
        final Player player = e.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))
            return;
        switch (e.getBlock().getType()) {
        case IRON_ORE: {
            e.getBlock().setType(Material.GLASS);
            dropCenter(new ItemStack(Material.IRON_INGOT, 1 + fortune_bonus(player)), e.getBlock().getLocation());
            ((ExperienceOrb) e.getBlock().getWorld().spawn(e.getBlock().getLocation(), ExperienceOrb.class))
                    .setExperience(2);
            break;
        }
        case GOLD_ORE: {
            e.getBlock().setType(Material.GLASS);
            dropCenter(new ItemStack(Material.GOLD_INGOT, 1 + fortune_bonus(player)), e.getBlock().getLocation());

            ((ExperienceOrb) e.getBlock().getWorld().spawn(e.getBlock().getLocation(), ExperienceOrb.class))
                    .setExperience(5);
            break;
        }
        default:{
            break;
        }
        }
    }
    //My style of cutclean is just turn natural stuff into cooked stuff. Not adding or changing the actual loot.
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(EntityDeathEvent e) {
        switch (e.getEntityType()) {
        case COW: {
            e.getDrops().forEach(it -> {
                if (it.getType() != Material.RAW_BEEF)
                    return;
                it.setType(Material.COOKED_BEEF);
            });
            break;
        }
        case CHICKEN: {
            e.getDrops().forEach(it -> {
                if (it.getType() != Material.RAW_CHICKEN)
                    return;
                it.setType(Material.COOKED_CHICKEN);
            });
            break;
        }
        case PIG: {
            e.getDrops().forEach(it -> {
                if (it.getType() != Material.PORK)
                    return;
                it.setType(Material.GRILLED_PORK);
            });
            break;
        }
        default:{
            break;
        }
        }
    }
    //Method that ensures ores don't fly like in many other servers
    void dropCenter(ItemStack itemStack, Location location) {
        Location centeredLocation = new Location(location.getWorld(), location.getBlockX() + 0.5,
                location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
        Item item = location.getWorld().dropItem(centeredLocation, itemStack);
        item.setVelocity(new Vector(0.0, 0.1, 0.0));
    }    
    //Quick int method to obtain what the fortune spell should bonus the player when they mine.
    int fortune_bonus(Player player) {
        ItemStack hand = player.getItemInHand();
        if (!hand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            return 0;
        int fortune_level = hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        int bonus = (int) (Math.random() * (fortune_level + 2)) - 1;
        if (bonus < 0) {
            bonus = 0;
        }
        return bonus;
    }

}