package me.infinityz.configuration;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityMountEvent;

import me.infinityz.UHC;
import net.md_5.bungee.api.ChatColor;

/**
 * GameConfigListener
 */
public class GameConfigListener implements Listener {
    // Make all the config things
    GameConfigManager gameConfigManager;

    public GameConfigListener(GameConfigManager gameConfigManager) {
        this.gameConfigManager = gameConfigManager;
    }

    // Apple and Flint rate
    @EventHandler
    public void rateAppleFlint(BlockBreakEvent e) {
        if (e.isCancelled())
            return;
        final Player player = e.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE
                || player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))
            return;
        switch (e.getBlock().getType()) {
        case LEAVES_2:
        case LEAVES: {
            // If less than 0 use vanilla
            if (gameConfigManager.gameConfig.apple_rate < 0)
                return;
            if (Math.random() <= (gameConfigManager.gameConfig.apple_rate)) {
                e.getBlock().setType(Material.GLASS);
                e.getBlock().breakNaturally();
                dropCenter(new ItemStack(Material.APPLE, 1 + fortune_bonus(player)), e.getBlock().getLocation());
            }
            break;
        }
        case GRAVEL: {
            // If less than 0 then use vanilla
            if (gameConfigManager.gameConfig.flint_rate < 0)
                return;
            if (Math.random() <= (gameConfigManager.gameConfig.flint_rate)) {
                e.getBlock().setType(Material.GLASS);
                e.getBlock().breakNaturally();
                dropCenter(new ItemStack(Material.FLINT, 1 + fortune_bonus(player)), e.getBlock().getLocation());
            }
            break;
        }
        default: {
            break;
        }
        }
    }

    // Applerate
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        if (UHC.getInstance().gameConfigManager.gameConfig.apple_rate < 0)
            return;
        if (Math.random() <= (UHC.getInstance().gameConfigManager.gameConfig.apple_rate)) {
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            dropCenter(new ItemStack(Material.APPLE, 1), e.getBlock().getLocation());
        }

    }

    // Horses
    @EventHandler
    public void onEntityMountEvent(EntityMountEvent e) {
        if (e.getEntity().getType() == EntityType.HORSE && !gameConfigManager.gameConfig.horses)
            e.setCancelled(true);
    }

    // HorseHealing and NaturalRegeneration
    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        if (!gameConfigManager.gameConfig.horsehealing && e.getEntityType() == EntityType.HORSE) {
            e.setCancelled(true);
            return;
        } else if (!gameConfigManager.gameConfig.natural_regeneration && e.getEntityType() == EntityType.PLAYER) {
            e.setCancelled(true);
        }
    }

    // Enderpearl damage
    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!gameConfigManager.gameConfig.ender_pearl_damage && e.getDamager().getType() == EntityType.ENDER_PEARL) {
            e.setCancelled(true);
            return;
        }
        // Handle friendly fire here?
    }

    // Golden Heads, Absorption is done at a packet level!
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if (e.getItem().getItemMeta() != null && e.getItem().getItemMeta().hasDisplayName() && e.getItem().getItemMeta()
                .getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&6golden head"))) {
            e.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
        }
    }

    // God apples and Golden Heads
    @EventHandler
    public void prepareCraftItem(CraftItemEvent e) {
        if (!gameConfigManager.gameConfig.godapples && e.getCurrentItem().getType() == Material.GOLDEN_APPLE
                && e.getCurrentItem().getDurability() == 1) {
            e.setCancelled(true);
            e.getWhoClicked().sendMessage("God apples are disabled!");
            return;
        }
        if (!gameConfigManager.gameConfig.goldenheads && e.getCurrentItem().getType() == Material.GOLDEN_APPLE
                && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().hasDisplayName()
                && e.getCurrentItem().getItemMeta().getDisplayName()
                        .equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&6golden head"))) {
            e.setCancelled(true);
            e.getWhoClicked().sendMessage("Golden heads are disabled!");
            return;
        }
    }
    //Potions
    @EventHandler
    public void brewEvent(BrewEvent e){
        if(!gameConfigManager.gameConfig.invisibility_potion || !gameConfigManager.gameConfig.regeneration_potion || !gameConfigManager.gameConfig.strength_1 ||!gameConfigManager.gameConfig.strength_2 ||!gameConfigManager.gameConfig.speed_1 ||!gameConfigManager.gameConfig.speed_2){
            for (ItemStack stack : e.getContents().getContents().clone()) {
                if(!gameConfigManager.gameConfig.invisibility_potion){
                    switch(stack.getDurability()){                        
                        case 8238:
                        case 8270:
                        case 16430:
                        case 16462:{
                            e.getContents().getViewers().forEach(it -> it.sendMessage("Regeneration potions are disabled!"));
                            e.getContents().remove(stack);
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                if(!gameConfigManager.gameConfig.regeneration_potion){
                    switch(stack.getDurability()){                        
                        case 8193:
                        case 8225:
                        case 8257:
                        case 16417:
                        case 16449:
                        case 16385:{
                            e.getContents().getViewers().forEach(it -> it.sendMessage("Regeneration potions are disabled!"));
                            e.getContents().remove(stack);
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                if(!gameConfigManager.gameConfig.speed_1){
                    switch(stack.getDurability()){                        
                        case 8194:
                        case 8226:
                        case 8258:
                        case 16386:
                        case 16418:
                        case 16450:{
                            e.getContents().getViewers().forEach(it -> it.sendMessage("Speed potions are disabled!"));
                            e.getContents().remove(stack);
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                else if(!gameConfigManager.gameConfig.speed_2){
                    switch(stack.getDurability()){                        
                        case 8226:
                        case 16418:{
                            e.getContents().getViewers().forEach(it -> it.sendMessage("Speed II potions are disabled!"));
                            e.getContents().remove(stack);
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                if(!gameConfigManager.gameConfig.strength_1){
                    switch(stack.getDurability()){                        
                        case 8201:
                        case 8233:
                        case 8265:
                        case 16393:
                        case 16425:
                        case 16457:{
                            e.getContents().getViewers().forEach(it -> it.sendMessage("Strength potions are disabled!"));
                            e.getContents().remove(stack);
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                else if(!gameConfigManager.gameConfig.strength_2){
                    switch(stack.getDurability()){                        
                        case 8233:
                        case 16425:{
                            e.getContents().getViewers().forEach(it -> it.sendMessage("Strength II potions are disabled!"));
                            e.getContents().remove(stack);
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                
            }
        }
    }

    // Method that ensures ores don't fly like in many other servers
    void dropCenter(ItemStack itemStack, Location location) {
        Location centeredLocation = new Location(location.getWorld(), location.getBlockX() + 0.5,
                location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
        Item item = location.getWorld().dropItem(centeredLocation, itemStack);
        item.setVelocity(new Vector(0.0, 0.2, 0.0));
    }

    // Quick int method to obtain what the fortune spell should bonus the player
    // when they mine.
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