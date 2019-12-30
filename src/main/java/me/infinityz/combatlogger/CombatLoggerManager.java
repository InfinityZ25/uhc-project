package me.infinityz.combatlogger;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import me.infinityz.UHC;
import net.minecraft.server.v1_8_R3.EntitySkeleton;

/**
 * CombatLoggerManager
 */
public class CombatLoggerManager {

    public SkeletonCombatLogger skeleton;

    public CombatLoggerManager(UHC instance){
        //Create a CombatloggerObject and save it if ever needed by external data
        skeleton = new SkeletonCombatLogger(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle());
        //Register the entity with entitynName CombatLogger("/summon CommandLogger" sirve.)
        skeleton.registerEntity("CombatLogger", 51, EntitySkeleton.class, SkeletonCombatLogger.class);
    }

}