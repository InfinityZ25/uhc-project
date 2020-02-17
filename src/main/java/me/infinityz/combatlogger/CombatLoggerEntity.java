package me.infinityz.combatlogger;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import me.infinityz.UHC;
import me.infinityz.player.UHCPlayer;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

/**
 * CombatLoggerEntity
 */
public class CombatLoggerEntity extends EntitySkeleton {
    public UUID player_Uuid;
    public PlayerInventory playerInventory;

    public CombatLoggerEntity(World world, Player player) {
        super(((CraftWorld) world).getHandle());
        this.player_Uuid = player.getUniqueId();
        this.playerInventory = player.getInventory();
        ((CraftLivingEntity) this.getBukkitEntity()).setRemoveWhenFarAway(false);
        UHC.getInstance().combatLoggerManager.inventory_map.put(this.getId(), this.playerInventory);
        UHCPlayer uhc = UHC.getInstance().playerManager.getUHCPlayerFromID(player.getUniqueId());
        if (uhc != null) {
            uhc.entity_combatlogger_id = this.getId();
        }

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

    @Override
    protected String z() {
        return "";
    }

    @Override
    public void collide(Entity entity) {
    }

    @Override
    public void g(double d0, double d1, double d2) {
    }

    @Override
    public void m() {
        super.m();
    }

    @Override
    protected void dropDeathLoot(boolean flag, int i) {
        Bukkit.getPluginManager().callEvent(new CombatLoggerDeathEvent(this.player_Uuid));
    }

}