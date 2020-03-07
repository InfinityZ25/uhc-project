package me.infinityz.scenarios.scenarios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.UHC;
import me.infinityz.scenarios.IScenario;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityChest;

/**
 * Timebomb
 */
public class Timebomb extends IScenario {

    public Timebomb() {
        this.description = "All player loot goes to a chest that blows up after 30 seconds.";
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent e) {
        // Make sure that we are in the right world to allow this
        if (e.getEntity().getWorld().getName().equalsIgnoreCase("lobby")
                || e.getEntity().getWorld().getName().equalsIgnoreCase("practice"))
            return;
        // Setup our variables
        final Player player = e.getEntity();
        final Location loc = player.getLocation();
        // Clone the drops to then clear them so that they won't, well, drop in the
        // floor.
        List<ItemStack> clonedDrops = new ArrayList<>(e.getDrops());
        e.getDrops().clear();
        // Add code that checks that there are no other chests nearby that could cause a
        // glithed inventory.
        Block chest1 = loc.getBlock();
        chest1.setType(Material.CHEST);
        Block chest2 = chest1.getRelative(getNonBlockedFace(chest1));
        chest2.setType(Material.CHEST);
        chest1.getRelative(BlockFace.UP).setType(Material.AIR);
        chest2.getRelative(BlockFace.UP).setType(Material.AIR);
        final Chest chest = (Chest) chest1.getState();
        // Store the armor contents so that it can be place first line of the inventory.
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        // Reverse it to make it go from helmet to boots instead of the other way
        // around.
        Collections.reverse(Arrays.asList(armorContents));
        // Removed from cloned drops and add it to the chest to keep the order
        for (ItemStack armor : armorContents) {
            if (armor != null && armor.getType() != Material.AIR)
                clonedDrops.remove(armor);
            chest.getInventory().addItem(new ItemStack[] { armor });
        }
        // Add the remaining cloned frops to the chest
        for (ItemStack stack : clonedDrops) {
            if (stack != null && stack.getType() != Material.AIR) {
                chest.getInventory().addItem(new ItemStack[] { stack });
            }
        }
        // In-progress, set metadata for the future owner of this chest
        chest.setMetadata("owner", new FixedMetadataValue(UHC.getInstance(),
                e.getEntity().getKiller() == null ? null : e.getEntity().getKiller().getUniqueId()));
        // Debug
        /*
         * chest.getMetadata("owner").forEach(it -> {
         * Bukkit.broadcastMessage("Owner might be " + it.asString()); });
         */
        // Make the inventory name of the chest prettier
        setName(chest1, ChatColor.GOLD + e.getEntity().getName() + "'s loot");
        new BukkitRunnable() {
            int time = 30;

            ArmorStand stand;

            @Override
            public void run() {
                if (time == 30) {
                    stand = summonHologram(loc);
                }
                time--;
                stand.setCustomName(time + "s");
                if (time <= 0) {
                    Bukkit.broadcastMessage("[Timebomb] " + player.getName() + "'s corpse has exploded!");
                    stand.remove();
                    loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 8F, false, true);
                    this.cancel();
                }
            }
        }.runTaskTimer(UHC.getInstance(), 0, 20L);
        // Continue with the logic later, add the explosion and the hologram.

    }

    ArmorStand summonHologram(Location loc) {
        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setSmall(true);
        stand.setVisible(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName("30s");
        stand.setGravity(false);
        stand.setCanPickupItems(false);
        stand.setNoDamageTicks(20 * 50);
        stand.setHealth(20.0);

        return stand;
    }

    BlockFace getNonBlockedFace(Block block) {
        if (block.getRelative(BlockFace.SOUTH).getType() != Material.BEDROCK) {
            return BlockFace.SOUTH;
        } else if (block.getRelative(BlockFace.WEST).getType() != Material.BEDROCK) {
            return BlockFace.WEST;
        } else if (block.getRelative(BlockFace.EAST).getType() != Material.BEDROCK) {
            return BlockFace.EAST;
        }
        return BlockFace.NORTH;
    }

    // This is NMS right now. Make it work across versions later
    void setName(Block block, String name) {
        if (block.getType() != Material.CHEST)
            return;
        // Get the NMS World
        net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
        // Get the tile entity
        TileEntity te = nmsWorld.getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        // Make sure it's a TileEntityChest before using it
        if (!(te instanceof TileEntityChest))
            return;
        ((TileEntityChest) te).a(name);
    }

}