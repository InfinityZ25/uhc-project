package me.infinityz.combatlogger;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.PlayerInventory;

import me.infinityz.UHC;

/**
 * CombatLoggerManager
 */
public class CombatLoggerManager {
    public Map<Integer, PlayerInventory> inventory_map = new HashMap<>();

    public CombatLoggerManager(UHC instance) {
    }

}