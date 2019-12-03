package me.infinityz.combatlogger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R3.World;

/**
 * SkeletonCombatLogger
 */
public class SkeletonCombatLogger extends EntitySkeleton{

    public SkeletonCombatLogger(World world){
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

    public PlayerInventory playerInventory;

    @Override
    protected String z() { return ""; }

    @Override
    public void collide(Entity entity) {
    }
    @Override
    public void g(double d0, double d1, double d2) {
    }

    @Override
    public void m(){
        super.m();
      }

    @Override
    protected void dropDeathLoot(boolean flag, int i) {
        Bukkit.broadcastMessage(this.getCustomName() + " has died!");
        if(playerInventory != null){

            //Continue here. Call a custom event, CombatLoggerDeathEvent(UUID uuid, PlayerInventory playerInventory)
            // And from then on, handle everything. Also, listen to entitydeathevent and check if name starts with CombatLogger.

        }
    }

    
    public Skeleton spawn(Location loc){
        World mcWorld = ((CraftWorld) loc.getWorld()).getHandle();
        final SkeletonCombatLogger customEnt = new SkeletonCombatLogger(mcWorld);

        customEnt.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftLivingEntity) customEnt.getBukkitEntity()).setRemoveWhenFarAway(false); //Do we want to remove it when the NPC is far away? I won
        mcWorld.addEntity(customEnt, SpawnReason.CUSTOM);
        return (Skeleton) customEnt.getBukkitEntity();
    }

    public Skeleton spawn(Player player){
        Location loc = player.getLocation();
        World mcWorld = ((CraftWorld) loc.getWorld()).getHandle();
        final SkeletonCombatLogger customEnt = new SkeletonCombatLogger(mcWorld);
        customEnt.playerInventory = player.getInventory();
        customEnt.setHealth(Float.parseFloat(player.getHealth() + ""));

        toInv(player.getInventory(), customEnt);

        customEnt.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftLivingEntity) customEnt.getBukkitEntity()).setRemoveWhenFarAway(false); //Do we want to remove it when the NPC is far away? I won
        mcWorld.addEntity(customEnt, SpawnReason.CUSTOM);
        return (Skeleton) customEnt.getBukkitEntity();        
    }

    public void toInv(PlayerInventory inventory, EntitySkeleton entitySkeleton){
        ItemStack boots = CraftItemStack.asNMSCopy(inventory.getBoots());
        ItemStack leggings = CraftItemStack.asNMSCopy(inventory.getLeggings());
        ItemStack chestplate = CraftItemStack.asNMSCopy(inventory.getChestplate());
        org.bukkit.inventory.ItemStack preHelmet = inventory.getHelmet();
        ItemMeta meta = preHelmet.getItemMeta();
        meta.spigot().setUnbreakable(true);
        preHelmet.setItemMeta(meta);
        ItemStack helmet = CraftItemStack.asNMSCopy(preHelmet);
        ItemStack hand = CraftItemStack.asNMSCopy(inventory.getItemInHand());
        entitySkeleton.setEquipment(0, hand);
        entitySkeleton.setEquipment(1, boots);
        entitySkeleton.setEquipment(2, leggings);
        entitySkeleton.setEquipment(3, chestplate);
        entitySkeleton.setEquipment(4, helmet);

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