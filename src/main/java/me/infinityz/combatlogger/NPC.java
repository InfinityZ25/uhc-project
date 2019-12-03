package me.infinityz.combatlogger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R3.World;


import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityTypes;

/**
 * NPC
 */
public class NPC extends EntityVillager {

    public NPC(World world) {
        super(world);

        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
   
            bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception e) {
            e.printStackTrace();
    }   
    }
    //Silence the entity on iddle
    @Override
    protected String z() { return ""; }    
    
    @Override
    public void collide(Entity entity) {
    }
    @Override
    public void g(double d0, double d1, double d2) {
    }
    
    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if(damagesource.p().equalsIgnoreCase(DamageSource.ANVIL.p())){
            Bukkit.broadcastMessage("is");
            return false;
        }
        super.damageEntity(damagesource, f);
        return true;
    }

    public Villager spawn(Location loc){
        World mcWorld = ((CraftWorld) loc.getWorld()).getHandle();
        final NPC customEnt = new NPC(mcWorld);
        customEnt.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftLivingEntity) customEnt.getBukkitEntity()).setRemoveWhenFarAway(false); //Do we want to remove it when the NPC is far away? I won
        mcWorld.addEntity(customEnt, SpawnReason.CUSTOM);
        return (Villager) customEnt.getBukkitEntity();
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
    return false;
    }

    
    public void registerEntity(String name, int id, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass){
        try {
     
            List<Map<?, ?>> dataMap = new ArrayList<Map<?, ?>>();
            for (Field f : EntityTypes.class.getDeclaredFields()){
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())){
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>) f.get(null));
                }
            }
     
            if (dataMap.get(2).containsKey(id)){
                dataMap.get(0).remove(name);
                dataMap.get(2).remove(id);
            }
     
            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);
     
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}