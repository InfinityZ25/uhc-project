package me.infinityz.combatlogger;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.ItemStack;

@SuppressWarnings("all")
public enum EntityTypes {
    // NAME("Entity name", Entity ID, yourcustomclass.class);
    COMBAT_LOGGER_ENTITY("SkeletonCombatLogger", 51, CombatLoggerEntity.class); // You can add as many as you want.

    private EntityTypes(String name, int id, Class<? extends Entity> custom) {
        addToMaps(custom, name, id);
    }

    public static void spawnEntity(Entity entity, Location loc) {
        entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        CombatLoggerEntity combat = (CombatLoggerEntity) entity;
        combat.setCustomName(Bukkit.getOfflinePlayer(combat.player_Uuid).getName());
        combat.setCustomNameVisible(true);
        handleInventory(combat);
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
    }

    static void handleInventory(CombatLoggerEntity entity) {
        ItemStack boots = CraftItemStack.asNMSCopy(entity.playerInventory.getBoots());
        ItemStack leggings = CraftItemStack.asNMSCopy(entity.playerInventory.getLeggings());
        ItemStack chestplate = CraftItemStack.asNMSCopy(entity.playerInventory.getChestplate());
        org.bukkit.inventory.ItemStack preHelmet = entity.playerInventory.getHelmet();
        // In case the helmet object is null or not existent, add a fake stick helmet to
        // avoid entity from flashing.
        if (preHelmet == null || preHelmet.getType() == Material.AIR)
            preHelmet = new org.bukkit.inventory.ItemStack(Material.STICK);
        ItemMeta meta = preHelmet.getItemMeta();
        if (meta != null) {
            meta.spigot().setUnbreakable(true);
            preHelmet.setItemMeta(meta);
        }
        ItemStack helmet = CraftItemStack.asNMSCopy(preHelmet);
        ItemStack hand = CraftItemStack.asNMSCopy(entity.playerInventory.getItemInHand());
        entity.setEquipment(0, hand);
        entity.setEquipment(1, boots);
        entity.setEquipment(2, leggings);
        entity.setEquipment(3, chestplate);
        entity.setEquipment(4, helmet);
    }

    private static void addToMaps(Class clazz, String name, int id) {
        // getPrivateField is the method from above.
        // Remove the lines with // in front of them if you want to override default
        // entities (You'd have to remove the default entity from the map first though).
        ((Map) getPrivateField("c", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(name, clazz);
        ((Map) getPrivateField("d", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(clazz, name);
        // ((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class,
        // null)).put(Integer.valueOf(id), clazz);
        ((Map) getPrivateField("f", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(clazz,
                Integer.valueOf(id));
        // ((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class,
        // null)).put(name, Integer.valueOf(id));
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }
}