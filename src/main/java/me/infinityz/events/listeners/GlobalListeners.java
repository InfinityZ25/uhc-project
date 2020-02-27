package me.infinityz.events.listeners;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.infinityz.UHC;
import me.infinityz.player.UHCPlayer;
import me.infinityz.player.UHCPlayerDisconnectEvent;
import me.infinityz.protocol.Reflection;
import me.infinityz.scenarios.events.ScenarioDisabledEvent;
import me.infinityz.scenarios.events.ScenarioEnabledEvent;
import me.infinityz.scoreboard.UHCBoard;

/**
 * GlobalListeners
 */
public class GlobalListeners extends SkeletonListener {
    public int chat_delay_ms;

    @SuppressWarnings("all")
    public GlobalListeners(final UHC instance) {
        super(instance);

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            final boolean isteam = UHC.getInstance().teamManager.team_enabled;
            instance.playerManager.players.values().forEach(all -> {
                if (all.alive && all.last_disconnect_time != null) {
                    if (System.currentTimeMillis() >= all.last_disconnect_time + (1000 * 60 * 10)) {
                        Player pl = Bukkit.getPlayer(all.uuid);
                        if (pl == null || !pl.isOnline()) {
                            all.alive = false;
                            all.died_time = System.currentTimeMillis();
                            final int new_aliv = UHC.getInstance().playerManager.getAlivePlayers();
                            final int teams_left = UHC.getInstance().playerManager.getTeamsLeft();
                            OfflinePlayer of = Bukkit.getOfflinePlayer(all.uuid);
                            Bukkit.broadcastMessage(of.getName() + " has died offline!");
                            instance.scoreboardManager.scoreboardMap.values().forEach(sb -> {
                                if (sb instanceof UHCBoard) {
                                    final UHCBoard uhcb = (UHCBoard) sb;
                                    uhcb.updatePlayersLeft(new_aliv);
                                    if (isteam) {
                                        uhcb.updateTeamsLeft(teams_left);
                                    }
                                }
                            });
                            // TODO: Call the even win?
                        }
                    }
                }
            });

        }, 20 * 30, 20 * 60);

        instance.executorService.scheduleAtFixedRate(new TimerTask() {
            Map<UUID, Collection<Vector>> map = new HashMap<>();
            UHC insta = instance;

            // Method that checks weather the player is near the wall or not
            boolean isNearWall(final Player p, final double wall, final double check_distance) {
                final Location loc = p.getLocation();
                // Get the absolute int of player's location and check if it is in proximity to
                // a "wall"
                if (Math.abs(loc.getX()) + check_distance >= wall || Math.abs(loc.getZ()) + check_distance >= wall)
                    return true;
                // Return the inverse.
                return false;
            }

            // Obtains the right blocks near to the player that must be set to crystal!
            List<Vector> getLocationalVectors(Location loc, int min_height, int max_height, int radius, int wall) {
                List<Vector> vector_list = new ArrayList<>();
                for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                    for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                        int absolute_x = Math.abs(x);
                        int absolute_z = Math.abs(z);
                        if (absolute_x != wall && absolute_z != wall)
                            continue;
                        if (absolute_x > wall || absolute_z > wall)
                            continue;
                        for (int y = loc.getBlockY() - min_height; y <= loc.getBlockY() + max_height; y++) {
                            int block_id = loc.getWorld().getBlockTypeIdAt(x, y, z);
                            if (block_id != 0)
                                continue;
                            vector_list.add(new Vector(x, y, z));
                        }
                    }
                }
                return vector_list;
            }

            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (isNearWall(player, instance.gameConfigManager.gameConfig.map_size - 3, 7)) {
                        List<Vector> collection = getLocationalVectors(player.getLocation(), 2, 3, 7,
                                instance.gameConfigManager.gameConfig.map_size);

                        if (map.containsKey(player.getUniqueId())) {
                            List<Vector> vList = new ArrayList<>(map.get(player.getUniqueId()));
                            vList.forEach(vector -> {
                                if (collection.contains(vector))
                                    return;
                                player.sendBlockChange(vector.toLocation(player.getWorld()), Material.AIR, (byte) 0);
                            });
                            vList = null;
                        }
                        collection.forEach(vector -> player.sendBlockChange(
                                vector.toLocation(player.getLocation().getWorld()), Material.STAINED_GLASS, (byte) 14));
                        map.put(player.getUniqueId(), collection);
                        return;
                    }
                    if (map.containsKey(player.getUniqueId())) {
                        List<Vector> vectors = new ArrayList<>(map.get(player.getUniqueId()));
                        vectors.forEach(vector -> player.sendBlockChange(vector.toLocation(player.getWorld()),
                                Material.AIR, (byte) 0));
                        vectors = null;
                        map.remove(player.getUniqueId());
                    }
                });
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        if (instance.keepLoaded.contains(e.getChunk())) {
            e.setCancelled(true);
        }
    }/*
      * 
      * @EventHandler public void onBreak(final BlockBreakEvent e) { if
      * (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.isCancelled()) return;
      * if (e.getBlock().getType().equals(Material.STONE)) {
      * e.getBlock().setType(Material.AIR); unbreaking_damage(e.getPlayer());
      * e.getBlock().getLocation().getWorld() .dropItem(new
      * Location(e.getBlock().getWorld(), e.getBlock().getX() + 0.5,
      * e.getBlock().getY() + 0.2, e.getBlock().getZ() + 0.5), new
      * ItemStack(Material.COBBLESTONE)) .setVelocity(new Vector(0.0, 0.2, 0.0)); } }
      */

    void unbreaking_damage(Player player) {
        ItemStack hand = player.getItemInHand();
        if (hand == null || hand.getType() == Material.AIR || !isTool(hand.getType()))
            return;
        if (!hand.containsEnchantment(Enchantment.DURABILITY)) {
            hand.setDurability((short) (hand.getDurability() + 1));
            player.updateInventory();
            return;
        }
        int unbreaking_level = hand.getEnchantmentLevel(Enchantment.DURABILITY);
        double chance = ((100 / (unbreaking_level + 1)) / 100.0D);
        int damage = Math.random() <= chance ? 1 : 0;
        hand.setDurability((short) (hand.getDurability() + damage));
        player.updateInventory();
    }

    boolean isTool(Material material) {
        if (material.toString().contains("PICKAXE") || material.toString().contains("AXE")
                || material.toString().contains("SPADE") || material.toString().contains("HOE"))
            return true;

        return false;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final UHCPlayer uhcPlayer = UHC.getInstance().playerManager.getUHCPlayerFromID(player.getUniqueId());
        if (uhcPlayer != null && uhcPlayer.alive && !uhcPlayer.spectator) {
            Bukkit.getPluginManager().callEvent(new UHCPlayerDisconnectEvent(e, uhcPlayer));
            uhcPlayer.last_disconnect_time = System.currentTimeMillis();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (!instance.gameConfigManager.gameConfig.nether) {
            event.setCancelled(true);
            return;
        }
        final TeleportCause portalType = event.getCause();
        if ((event.getTo() != null && event.getTo().getWorld().getEnvironment() == Environment.THE_END)
                || portalType == TeleportCause.END_PORTAL) {
            // Handle end in the future?
        } else {
            processNetherPortalEvent(event);
        }
    }

    @EventHandler
    public void scenEnable(ScenarioEnabledEvent e) {
        Bukkit.broadcastMessage(e.getScenario().getClass().getSimpleName() + " has been enabled");
    }

    @EventHandler
    public void scenDisable(ScenarioDisabledEvent e) {
        Bukkit.broadcastMessage(e.getScenario().getClass().getSimpleName() + " has been disabled");
    }

    private void processNetherPortalEvent(final PlayerPortalEvent event) {
        final Player player = event.getPlayer();
        final World fromWorld = player.getWorld();
        World toWorld;
        final Environment oldEnvironment;
        boolean useDimension = true;

        oldEnvironment = player.getWorld().getEnvironment() == Environment.NETHER ? Environment.NETHER
                : Environment.NORMAL;
        final String toWorldName;
        if (oldEnvironment == Environment.NETHER) {
            toWorldName = player.getWorld().getName().replaceFirst("_nether", "");
        } else {
            toWorldName = player.getWorld().getName().concat("_nether");
        }
        toWorld = Bukkit.getWorld(toWorldName);
        if (toWorld == null) {
            player.sendMessage("There isn't a world for us to teleport you to.");
            return;
        }
        if (toWorld.getEnvironment().equals(fromWorld.getEnvironment()))
            useDimension = false;

        final double blockRatio = useDimension ? (oldEnvironment == Environment.NETHER ? 8 : 0.125) : 1;

        final Location fromLocation = new Location(fromWorld, player.getLocation().getX(), player.getLocation().getY(),
                player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        final Location toLocation = checkOutside(new Location(toWorld, (player.getLocation().getX() * blockRatio),
                player.getLocation().getY(), (player.getLocation().getZ() * blockRatio), player.getLocation().getYaw(),
                player.getLocation().getPitch()), instance.gameConfigManager.gameConfig.map_size);

        final List<Block> blocks = findPortalBlocks(toLocation);

        blocks.forEach(block -> {
            for (final BlockFace face : BlockFace.values()) {
                final Block gb = block.getRelative(face);
                if (gb.getType() != Material.OBSIDIAN && gb.getType() != Material.PORTAL
                        && gb.getType() != Material.AIR) {
                    gb.setType(Material.AIR);
                }
            }
        });
        event.setTo(toLocation);
        event.setFrom(fromLocation);
        event.useTravelAgent(true);
    }

    @EventHandler
    public void e(final PortalCreateEvent e) {

    }

    List<Block> findPortalBlocks(final Location loc) {
        return findPortalBlocks(loc.getWorld().getBlockAt(loc));
    }

    List<Block> findPortalBlocks(final Block loc) {
        final List<Block> lBlocks = new ArrayList<>();
        final int radius = 15;
        for (int x = loc.getX() - radius; x <= loc.getX() + radius; x++) {
            for (int y = loc.getY() - radius; y <= loc.getY() + radius; y++) {
                for (int z = loc.getZ() - radius; z <= loc.getZ() + radius; z++) {
                    final Block b = loc.getWorld().getBlockAt(x, y, z);
                    if (b.getType() == Material.OBSIDIAN || b.getType() == Material.PORTAL) {
                        lBlocks.add(b);
                    }
                }
            }
        }

        return lBlocks;
    }

    // Make sure no portals are being generated outside the world border.
    // Use a simple integer check no ensure it.
    int checkOutside(final int number, final int max) {
        if (number >= max && number > 0)
            return max;
        else if ((number < 0 && number <= (-max)))
            return -max;
        return number;
    }

    // Check a location with a function that takes a location object, instead of
    // checking each number manually.
    Location checkOutside(final Location loc, final int max) {
        loc.setX(checkOutside(loc.getBlockX(), max));
        loc.setZ(checkOutside(loc.getBlockZ(), max));
        return loc;
    }

    @EventHandler
    public void portalCreateEvent(
            final PortalCreateEvent e) {/*
                                         * e.getBlocks().forEach(at -> {
                                         * at.getRelative(BlockFace.EAST).setType(Material.GLOWSTONE); });
                                         */

    }

    // Handle the old-style enchanting table starts.
    @EventHandler
    public void inventoryClickEvent(final InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof EnchantingInventory) {
            if (e.getSlot() == 1) {
                try {

                    final Class<?> anvilClass = Reflection.getClass("{nms}.ContainerEnchantTable");

                    final Field field = anvilClass.getField("use_lapis");
                    final boolean bol = field.getBoolean(anvilClass);
                    if (!bol)
                        e.setCancelled(true);

                } catch (final Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        e.getPlayer().setNoDamageTicks(18);
        e.setJoinMessage("");
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        e.setQuitMessage("");
        instance.scoreboardManager.scoreboardMap.remove(e.getPlayer().getUniqueId());
        instance.scoreboardManager.fastBoardMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void w(final WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeather(final ThunderChangeEvent e) {
        e.setCancelled(true);
    }

}