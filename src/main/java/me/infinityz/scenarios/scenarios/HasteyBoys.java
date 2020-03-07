package me.infinityz.scenarios.scenarios;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.infinityz.scenarios.IScenario;

/**
 * HasteyBoys
 */
public class HasteyBoys extends IScenario {

    public HasteyBoys() {
        this.description = "All tools come pre-enchanted with Efficieny and Unbreaking 3";
    }

    @EventHandler
    public void craftItemEvent(PrepareItemCraftEvent e) {
        if (isTool(e.getInventory().getResult().getType())) {
            ItemStack stack = e.getInventory().getResult().clone();
            ItemMeta meta = stack.getItemMeta();
            meta.addEnchant(Enchantment.DIG_SPEED, 3, true);
            meta.addEnchant(Enchantment.DURABILITY, 3, true);
            stack.setItemMeta(meta);
            e.getInventory().setResult(stack);
        }
    }

    boolean isTool(Material material) {
        if (material.toString().contains("PICKAXE") || material.toString().contains("AXE")
                || material.toString().contains("SPADE") || material.toString().contains("HOE"))
            return true;

        return false;
    }

}