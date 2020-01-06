package me.infinityz.practice;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.infinityz.UHC;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;

/**
 * PracticeManager
 */
public class PracticeManager implements Listener {

    private UHC instance;
    public World practice_world;
    public boolean enabled;
    public int teleport_radius;
    public HashSet<UUID> practiceHashSet;

    // Extend as a Listener for events, make sure to give the right priority to
    // avoid other events interception.
    public PracticeManager(UHC instance) {
        this.instance = instance;
        this.enabled = false;
        this.practice_world = instance.getServer()
                .createWorld(new WorldCreator("Practice").type(WorldType.FLAT).generateStructures(false));
        this.practice_world.setGameRuleValue("doMobSpawning", "false");
        this.practice_world.setGameRuleValue("doFireTick", "false");
        this.practice_world.setGameRuleValue("doDaylightCycle", "false");
        this.practice_world.setGameRuleValue("naturalRegeneration", "false");
        this.teleport_radius = 100;
        this.practiceHashSet = new HashSet<>();

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        e.setQuitMessage("");
        // Check if the player is in the arena
        if (practiceHashSet.contains(player.getUniqueId())) {
            // Kill the player and remove from arena list.
            player.damage(player.getHealth());
            player.spigot().respawn();
            practiceHashSet.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // Set the death message to empty so all players won't see it.
        e.setDeathMessage("");
        // Make sure player that died is in practice
        if (!isInPractice(e.getEntity().getUniqueId()))
            return;
        final Player player = e.getEntity();
        // Call the method that takes care of the death of the player
        handleDeath(e);
        // Ensure the killer is a player and not anyother kindof entity
        if (player.getKiller() == null || !(player.getKiller() instanceof Player))
            return;
        final Player killer = player.getKiller();
        // Now that we know who the killer is, reward them.
        if (isInPractice(killer.getUniqueId())) {
            // TODO: MAKE THE REWARD SYSTEM, GIVE SOMETHING TO KILLER
            killer.sendMessage("You've killed " + player.getDisplayName() + "!");
            return;
        }
        killer.sendMessage(
                "A player that you damaged has died and you technically did kill him. Congratulations I guess.");

    }

    @EventHandler
    public void onBucketEvent(PlayerBucketEmptyEvent e) {
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            e.getBlockClicked().getRelative(e.getBlockFace()).setType(Material.AIR);
            e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().first(Material.BUCKET),
                    new ItemStack(e.getBucket()));
        }, 2L);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        e.setCancelled(true);
    }

    public void joinPractice(final Player p) {
        // Ensure the player is not already in the hashset
        if (practiceHashSet.contains(p.getUniqueId())) {
            p.sendMessage("You are already in practice!");
            return;
        }
        assert p.isOnline();
        p.teleport(getLocation());
        givePracticeKit(p);

        practiceHashSet.add(p.getUniqueId());
        p.sendMessage("Welcome to practice buddy!");
    }

    public void joinPractice(final UUID uuid) {
        // Recursive method for the uuid.
        joinPractice(Bukkit.getPlayer(uuid));
    }

    public void leavePractice(final Player player) {
        if (!isInPractice(player.getUniqueId()))
            return;
        player.damage(player.getHealth());
        player.setHealth(20.0D);
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0.0, 2.0, 0.0));
        player.sendMessage("You've left practice!");
        practiceHashSet.remove(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            wipePlayer(player);
        }, 10L);
    }

    public boolean isInPractice(UUID uuid) {
        return practiceHashSet.contains(uuid);
    }

    void handleDeath(PlayerDeathEvent e) {
        final Player player = e.getEntity();
        // Canel items from dropping
        e.getDrops().clear();
        // Set the player's health back up to 20.0D to avoid a respawn screen
        player.setHealth(20.0D);
        // Use lambda to save lines of code. Verify if player hasn't gone offline and
        // then proceed.

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            if (!player.isOnline())
                return;
            player.sendMessage("You were killed by "
                    + (player.getKiller() == null ? "mystical causes!" : player.getKiller().getDisplayName() + "!"));
            givePracticeKit(player);

        }, 5L);

        playEntityDeathAnimation(player);

        player.teleport(getLocation());

    }

    void playEntityDeathAnimation(Player player) {
        // Send packet
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        CraftPlayer craft = (CraftPlayer) player;
        EntityPlayer npc = new EntityPlayer(server, world, craft.getProfile(), new PlayerInteractManager(world));
        Location loc = player.getLocation();
        npc.setPosition(loc.getX(), loc.getY(), loc.getZ());

        PacketPlayOutEntityStatus death = new PacketPlayOutEntityStatus(npc, (byte) 3);
        Bukkit.getOnlinePlayers().forEach(all -> {
            if (all == player || all.getWorld() != practice_world)
                return;
            CraftPlayer craft2 = (CraftPlayer) all;
            craft2.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            craft2.getHandle().playerConnection.sendPacket(death);
        });

    }

    void givePracticeKit(final Player player) {
        wipePlayer(player);
        final PlayerInventory inventory = player.getInventory();
        inventory.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        inventory.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        inventory.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        inventory.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        inventory.setHeldItemSlot(0);
        inventory.setItem(0, new ItemStack(Material.DIAMOND_SWORD));
        inventory.setItem(1, new ItemStack(Material.FISHING_ROD));
        inventory.setItem(2, new ItemStack(Material.BOW));

        inventory.setItem(4, new ItemStack(Material.WATER_BUCKET));
        inventory.setItem(5, new ItemStack(Material.COOKED_BEEF, 32));
        inventory.setItem(9, new ItemStack(Material.ARROW, 16));
    }

    void wipePlayer(final Player player) {
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setHealth(20.0D);
        player.setSaturation(20.0F);
        player.getActivePotionEffects().forEach(effect -> {
            player.removePotionEffect(effect.getType());
        });
    }

    public Location getLocation() {
        Location loc = new Location(practice_world, 0, 0, 0);
        loc.setX(loc.getX() + Math.random() * teleport_radius * 2.0 - teleport_radius);
        loc.setZ(loc.getZ() + Math.random() * teleport_radius * 2.0 - teleport_radius);
        return loc.getWorld().getHighestBlockAt(loc).getLocation().add(0.0, 2.0, 0.0);
    }

}