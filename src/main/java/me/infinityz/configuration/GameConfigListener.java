package me.infinityz.configuration;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.infinityz.UHC;

/**
 * GameConfigListener
 */
public class GameConfigListener implements Listener{

    //Make all the config things

    @EventHandler
    public void rateAppleFlint(BlockBreakEvent e){
        if(e.isCancelled())return;
        final Player player = e.getPlayer();        
        if (player.getGameMode() == GameMode.CREATIVE || player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))
            return;
        switch(e.getBlock().getType()){
            case LEAVES_2:
            case LEAVES:{
                //If less than 0 use vanilla
                if(UHC.getInstance().gameConfigManager.gameConfig.apple_rate  < 0 )return;
                if(Math.random() <= (UHC.getInstance().gameConfigManager.gameConfig.apple_rate)){
                    e.getBlock().setType(Material.GLASS);
                    e.getBlock().breakNaturally();
                    dropCenter(new ItemStack(Material.APPLE, 1 + fortune_bonus(player)), e.getBlock().getLocation());
                }
                break;
            }
            case GRAVEL:{
                //If less than 0 then use vanilla
                if(UHC.getInstance().gameConfigManager.gameConfig.flint_rate  < 0)return;
                if(Math.random() <= (UHC.getInstance().gameConfigManager.gameConfig.flint_rate)){
                    e.getBlock().setType(Material.GLASS);
                    e.getBlock().breakNaturally();
                    dropCenter(new ItemStack(Material.FLINT, 1 + fortune_bonus(player)), e.getBlock().getLocation());           
                }
                break;
            }
            default:{
                break;
            }
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e){
        if(UHC.getInstance().gameConfigManager.gameConfig.apple_rate < 0) return;
        if(Math.random() <= (UHC.getInstance().gameConfigManager.gameConfig.apple_rate)){
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            dropCenter(new ItemStack(Material.APPLE, 1), e.getBlock().getLocation());
        }

    }
    @EventHandler
    public void onHorse(PlayerInteractAtEntityEvent e){
        if(e.getRightClicked() instanceof Horse){
            if(UHC.getInstance().gameConfigManager.gameConfig.horses){
                e.setCancelled(true);
            }            
        }        
    }
    
    //Method that ensures ores don't fly like in many other servers
    void dropCenter(ItemStack itemStack, Location location) {
        Location centeredLocation = new Location(location.getWorld(), location.getBlockX() + 0.5,
                location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
        Item item = location.getWorld().dropItem(centeredLocation, itemStack);
        item.setVelocity(new Vector(0.0, 0.2, 0.0));
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