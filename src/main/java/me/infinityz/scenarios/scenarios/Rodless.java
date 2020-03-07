package me.infinityz.scenarios.scenarios;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.infinityz.scenarios.IScenario;

/**
 * Bowless
 */
public class Rodless extends IScenario {

    public Rodless() {
        this.description = "Fishing rods are not craftable or usable.";
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
                && event.getPlayer().getItemInHand().getType() == Material.FISHING_ROD) {
            event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void craftItemEvent(PrepareItemCraftEvent e) {
        if (e.getInventory().getResult() == null || e.getInventory().getResult().getType() != Material.FISHING_ROD)
            return;
        e.getInventory().setResult(null);
        e.getViewers().get(0).sendMessage("Rodless is enabled!");
    }

}