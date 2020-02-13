package me.infinityz.scenarios.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.infinityz.scenarios.IScenario;
import net.md_5.bungee.api.ChatColor;

/**
 * GHeads
 */
public class GHeads extends IScenario {
    ItemStack item = new ItemStack(Material.GOLDEN_APPLE);

    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(PlayerDeathEvent e) {
        e.getDrops().add(item);
        // Obviously not done. But the principle works with timebomb no problem. Just
        // implement the rest of the code

    }

    @SuppressWarnings("all")
    @Override
    public void enableScenario() {
        super.enableScenario();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Golden Head"));
        item.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(item);
        recipe.shape("G", "G", "G", "G", "H", "G", "G", "G", "G");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('H', Material.SKULL_ITEM, 3);
        Bukkit.addRecipe(recipe);
    }
    @Override
    public void disableScenario(){
        super.disableScenario();
        Bukkit.clearRecipes();
    }

}