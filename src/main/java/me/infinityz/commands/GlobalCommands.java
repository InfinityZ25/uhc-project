package me.infinityz.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import me.infinityz.UHC;
import me.infinityz.UHC.GameStage;
import me.infinityz.border.BedrockBorderTask;
import me.infinityz.configuration.GameConfigCMD;
import me.infinityz.logic.PreGameStartEvent;
import me.infinityz.player.UHCPlayer;
import me.infinityz.protocol.Reflection;
import me.infinityz.scatter.Scatter;
import me.infinityz.scenarios.IScenario;
import me.infinityz.scenarios.events.ScenarioDisabledEvent;
import me.infinityz.scenarios.events.ScenarioEnabledEvent;
import me.infinityz.scoreboard.UHCBoard;
import net.md_5.bungee.api.ChatColor;

/**
 * GlobalCommands
 */

@SuppressWarnings("all")
public class GlobalCommands implements CommandExecutor {

    private final UHC instance;
    boolean es = false;
    int task_id = 0;

    public GlobalCommands(final UHC instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("uhc")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bThis server is runing uhc-project version 2019.12.1.1-A"));
            // Make sure that the sender is not the console beyond this point
            if (!sender.hasPermission("uhc.admin")) {
                return true;
            }
            assert sender instanceof Player;
            final Player player = (Player) sender;
            // Obtain, if exist, the player's scoreboard.
            switch (args[0].toLowerCase()) {
                case "border": {
                    final int i = Integer.parseInt(args[1]);
                    final int wall_size = Integer.parseInt(args[2]);
                    // putWall(player.getLocation().getWorld(), i, wall_size);
                    final BedrockBorderTask borderTask = new BedrockBorderTask(player.getLocation().getWorld(), i,
                            wall_size, Integer.parseInt(args[3]));
                    borderTask.runTaskTimer(instance, 0, Integer.parseInt(args[4]));
                    break;
                }
                case "packets": {
                    instance.map.forEach((pack, in) -> {
                        sender.sendMessage(pack + ": " + in);
                    });
                    break;
                }
                case "pc": {
                    instance.map.clear();

                    break;
                }
                case "holo": {
                    instance.createHolo(player, "");
                    break;
                }
                case "bedrock": {
                    if (args.length < 3) {
                        sender.sendMessage("Usage: /uhc bedrock <world> <size> <height>");
                        return true;
                    }
                    final BedrockBorderTask borderTask = new BedrockBorderTask(Bukkit.getWorld(args[1]),
                            Integer.parseInt(args[2]), Integer.parseInt(args[3]), 200);
                    borderTask.runTaskTimer(instance, 0, 20 * 2);

                    break;
                }
                case "chunks": {

                    break;
                }
                case "find": {
                    new Scatter(Bukkit.getWorld("UHC"), UHC.getInstance().gameConfigManager.gameConfig.map_size, 100,
                            Integer.parseInt(args[1]), 150).runTaskTimer(UHC.getInstance(), 40L, 20L);
                    break;
                }
                case "scatter": {
                    sender.sendMessage(UHC.getInstance().keepLoaded.size() + " chunks are being kept loaded atm!");
                    break;
                }
                case "practice": {
                    if (instance.practiceManager.isInPractice(player.getUniqueId())) {
                        instance.practiceManager.leavePractice(player);
                        break;
                    }
                    instance.practiceManager.joinPractice(player);
                    break;
                }
                case "tpw": {
                    player.teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
                    break;
                }
                case "start": {
                    if (GameStage.stage != GameStage.LOBBY) {
                        sender.sendMessage("You can't start the game when gamestage is " + GameStage.stage.toString());
                        return true;
                    }
                    if (instance.locations == null || instance.locations.isEmpty()) {
                        sender.sendMessage("You can't start the game with no locations loaded!");

                        return true;
                    }
                    if (instance.practiceManager.enabled) {
                        // Disable an unload practice
                        UHC.getInstance().practiceManager.enabled = false;
                        // Ensure that no errors occur by clonning the hashset and interating
                        new HashSet<>(UHC.getInstance().practiceManager.practiceHashSet).forEach(it -> {
                            final Player pl = Bukkit.getPlayer(it);
                            if (pl != null && pl.isOnline()) {
                                UHC.getInstance().practiceManager.leavePractice(pl);
                            }
                        });
                        UHC.getInstance().practiceManager.practiceHashSet.clear();
                        if (UHC.getInstance().teamManager.team_enabled) {
                            UHC.getInstance().teamManager.team_management = false;
                        }
                        HandlerList.unregisterAll(UHC.getInstance().practiceManager.practiceListener);
                    }
                    // Whitelist everyone and clear the whitelistors
                    instance.whitelistManager.whitelist_enabled = true;
                    Bukkit.getOnlinePlayers().stream().forEach(all -> {
                        instance.whitelistManager.whitelist.add(all.getUniqueId());
                    });
                    instance.whitelistManager.whitelistorPlayers.clear();
                    // Let the server know that the game is starting
                    // Maybe call an event??
                    GameStage.stage = GameStage.PRE_GAME;
                    // Take it to the scatter task from now on!
                    // Call PreGameStartEvent;
                    Bukkit.getPluginManager().callEvent(new PreGameStartEvent(sender));

                    break;
                }
                case "host": {
                    final OfflinePlayer of = Bukkit.getOfflinePlayer(args[1]);
                    instance.gameConfigManager.host = of.getUniqueId();
                    instance.gameConfigManager.last_known_host_name = of.getName();
                    break;
                }

                case "show_enchants": {

                    final Class<?> tableClass = Reflection.getClass("{nms}.ContainerEnchantTable");
                    try {
                        final Field field = tableClass.getDeclaredField("show_enchants");
                        field.setAccessible(true);
                        field.set(null, Boolean.parseBoolean(args[1]));

                    } catch (final Exception e) {
                    }
                    break;
                }

                case "fix": {
                    final Location pl = player.getLocation();
                    player.teleport(Bukkit.getWorld("Practice").getSpawnLocation());
                    Bukkit.getScheduler().runTaskLater(instance, () -> {
                        player.teleport(pl);
                    }, 2L);
                    break;
                }
                case "kt": {
                    final Map<UUID, Integer> tm = instance.playerManager.getKT();
                    final LinkedHashMap<UUID, Integer> ltm = new LinkedHashMap<>();
                    tm.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10)
                            .forEachOrdered(x -> ltm.put(x.getKey(), x.getValue()));
                    int i = 0;
                    for (final Entry<UUID, Integer> entry : ltm.entrySet()) {
                        i++;
                        sender.sendMessage(i + ". " + Bukkit.getOfflinePlayer(entry.getKey()).getName() + ": "
                                + entry.getValue() + " kills");
                    }
                    break;
                    /*
                     * instance.playerManager.getKT().forEach((uuid, kills) -> {
                     * sender.sendMessage(Bukkit.getOfflinePlayer(uuid).getName() + " has made " +
                     * kills); });
                     */
                }

                case "old_levels": {

                    final Class<?> tableClass = Reflection.getClass("{nms}.ContainerEnchantTable");
                    try {
                        final Field field = tableClass.getDeclaredField("old_levels");
                        field.setAccessible(true);
                        field.set(null, Boolean.parseBoolean(args[1]));

                    } catch (final Exception e) {
                    }
                    break;
                }
                case "scenario": {
                    final IScenario scenario = instance.scenariosManager.scenarioMap.get(args[1]);
                    if (scenario.enabled) {
                        scenario.disableScenario();
                        Bukkit.getPluginManager().callEvent(new ScenarioDisabledEvent(scenario, (Player) sender));
                    } else {
                        scenario.enableScenario();
                        Bukkit.getPluginManager().callEvent(new ScenarioEnabledEvent(scenario, (Player) sender));
                    }

                    break;
                }
                case "respawn": {
                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    if (offlinePlayer != null) {
                        final UHCPlayer uhcPlayer = instance.playerManager
                                .getUHCPlayerFromID(offlinePlayer.getUniqueId());
                        if (uhcPlayer != null) {
                            uhcPlayer.alive = true;
                            Bukkit.broadcastMessage("[UHC] " + offlinePlayer.getName() + " has been respawned!");
                            // Update player's scoreboard
                            final int new_aliv = UHC.getInstance().playerManager.getAlivePlayers();
                            final int new_team = UHC.getInstance().playerManager.getTeamsLeft();
                            final boolean team = UHC.getInstance().teamManager.team_enabled;
                            UHC.getInstance().scoreboardManager.scoreboardMap.values().forEach(sb -> {
                                if (sb instanceof UHCBoard) {
                                    final UHCBoard uhcb = (UHCBoard) sb;
                                    uhcb.updatePlayersLeft(new_aliv);
                                    if (team) {
                                        uhcb.updateTeamsLeft(new_team);
                                    }
                                }
                            });
                            if (offlinePlayer.isOnline()) {
                                final Player onlinePlayer = offlinePlayer.getPlayer();
                                onlinePlayer.getInventory().setContents(uhcPlayer.death_Inventory);

                                onlinePlayer.getInventory().setArmorContents(uhcPlayer.armour);
                                onlinePlayer.teleport(uhcPlayer.death_location);
                            } else {
                                // Queue them up!
                            }
                            return true;
                        }
                        sender.sendMessage(args[1] + " has not played in this game!");
                        return true;
                    }
                    break;
                }
                case "rate": {
                    switch (args[1].toLowerCase()) {
                        case "apple": {
                            Bukkit.broadcastMessage("Applec rate: " + instance.gameConfigManager.gameConfig.apple_rate);
                            instance.gameConfigManager.gameConfig.apple_rate = Double.parseDouble(args[2]);
                            Bukkit.broadcastMessage(
                                    "New Apple rate: " + instance.gameConfigManager.gameConfig.apple_rate);
                            break;
                        }
                        case "flint": {
                            Bukkit.broadcastMessage("Flint rate: " + instance.gameConfigManager.gameConfig.flint_rate);

                            instance.gameConfigManager.gameConfig.flint_rate = Double.parseDouble(args[2]);
                            Bukkit.broadcastMessage(
                                    "New Flint rate: " + instance.gameConfigManager.gameConfig.flint_rate);
                            break;
                        }
                    }
                    break;
                }
                case "velocity": {/*
                                   * // Get velocity unit vector: Vector unitVector =
                                   * player.getLocation().toVector().subtract(player.getLocation().toVector())
                                   * .normalize(); // Set speed and push entity:
                                   * player.setVelocity(unitVector.multiply(Double.parseDouble(args[1])));
                                   */
                    player.setVelocity(player.getLocation().getDirection().multiply(Double.parseDouble(args[1])));
                    // knockBack(player, player.getLocation());
                    break;
                }

            }

            return true;
        }
        if (cmd.getName().equalsIgnoreCase("heal")) {
            if (!sender.hasPermission("uhc.heal")) {
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage("Command usage: /heal <all:player>");
                return true;
            }
            if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("*")) {
                Bukkit.getOnlinePlayers().forEach(all -> {
                    all.setHealth(20.0D);
                });
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
                target.setHealth(20.0D);
                return true;
            }

            return false;
        }
        if (cmd.getName().equalsIgnoreCase("feed")) {
            if (!sender.hasPermission("uhc.feed")) {
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage("Command usage: /feed <all:player>");
                return true;
            }
            if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("*")) {
                Bukkit.getOnlinePlayers().forEach(all -> {
                    all.setFoodLevel(20);
                });
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
                target.setFoodLevel(20);
                return true;
            }

            return false;
        }
        if (cmd.getName().equalsIgnoreCase("msg")) {
            if (args.length < 1) {
                sender.sendMessage("Correct usage: /msg <Player> [Message]");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
                if (target.getName().equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage(ChatColor.RED + "Can't message yourself!");
                    return true;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b<player>&7->&bMe&7] &b<message>"
                        .replace("<player>", sender.getName()).replace("<message>", sb.toString().trim())));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bMe&7->&b<player>&7] &b<message>"
                        .replace("<player>", sender.getName()).replace("<message>", sb.toString().trim())));
                return true;

            }
            sender.sendMessage(ChatColor.RED + args[0] + " is not online!");

            return true;
        }
        if (cmd.getName().equalsIgnoreCase("helpop")) {
            if (args.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }

                Bukkit.broadcast(ChatColor.DARK_RED + "[Helpop] " + ChatColor.WHITE + sender.getName() + ": "
                        + ChatColor.GRAY + sb.toString().trim(), "uhc.helpop.recieve");
                sender.sendMessage(ChatColor.GREEN + "Your message was sent!");
                return true;
            }
            sender.sendMessage("Correct usage: /helpop <Message>");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("respawn")) {
            if (!sender.hasPermission("uhc.respawn")) {
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage("Command usage: /respawn <player>");
                return true;
            }
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            if (offlinePlayer != null) {
                final UHCPlayer uhcPlayer = instance.playerManager.getUHCPlayerFromID(offlinePlayer.getUniqueId());
                if (uhcPlayer != null) {
                    uhcPlayer.alive = true;
                    Bukkit.broadcastMessage("[UHC] " + offlinePlayer.getName() + " has been respawned!");
                    // Update player's scoreboard
                    final int new_aliv = UHC.getInstance().playerManager.getAlivePlayers();
                    UHC.getInstance().scoreboardManager.scoreboardMap.values().forEach(sb -> {
                        if (sb instanceof UHCBoard) {
                            final UHCBoard uhcb = (UHCBoard) sb;
                            uhcb.updatePlayersLeft(new_aliv);
                        }
                    });
                    if (offlinePlayer.isOnline()) {
                        final Player onlinePlayer = offlinePlayer.getPlayer();
                        onlinePlayer.getInventory().setContents(uhcPlayer.death_Inventory);

                        onlinePlayer.getInventory().setArmorContents(uhcPlayer.armour);
                        onlinePlayer.teleport(uhcPlayer.death_location);
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            all.showPlayer(onlinePlayer);
                        });
                    } else {
                        // Queue them up! to be respawned when they reconect
                        instance.playerManager.pending_respawns.add(offlinePlayer.getUniqueId());
                        instance.whitelistManager.whitelist.add(offlinePlayer.getUniqueId());
                    }
                    return true;
                }
                sender.sendMessage(args[1] + " has not played in this game!");
                return true;
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("latescatter")) {
            if (!sender.hasPermission("uhc.dq")) {
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {

            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("invsee")) {
            if (!sender.hasPermission("uhc.dq")) {
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
                Inventory inv = Bukkit.createInventory(null, 9 * 5, ChatColor.GOLD + target.getName() + "'s inventory");
                inv.setContents(target.getInventory().getContents());
                for (int i = 0; i < 4; i++) {
                    inv.setItem(36 + i, target.getInventory().getArmorContents()[i]);
                }
                ItemStack i = new ItemStack(Material.NETHER_STAR);
                ItemMeta meta = i.getItemMeta();
                meta.setDisplayName("Potion Effects:");
                List<String> s = new ArrayList<>();
                target.getActivePotionEffects().forEach(pf -> {
                    s.add(pf.getType().getName() + ": " + GameConfigCMD.formatTime(pf.getDuration() / 20));
                });
                meta.setLore(s);
                i.setItemMeta(meta);
                inv.setItem(41, i);
                Player player = (Player) sender;
                player.openInventory(inv);
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("dq")) {
            if (!sender.hasPermission("uhc.dq")) {
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage("Command usage: /dq <player>");
                return true;
            }

            OfflinePlayer of = Bukkit.getOfflinePlayer(args[0]);
            if (of != null) {
                UHCPlayer uhc = instance.playerManager.getUHCPlayerFromID(of.getUniqueId());
                if (uhc != null) {
                    uhc.alive = false;
                    instance.whitelistManager.whitelist.remove(uhc.uuid);
                    Bukkit.broadcastMessage("[UHC] " + of.getName() + " has been dq'd!");
                    final int new_aliv = UHC.getInstance().playerManager.getAlivePlayers();
                    final boolean isteam = UHC.getInstance().teamManager.team_enabled;
                    final int teams_left = UHC.getInstance().playerManager.getTeamsLeft();
                    UHC.getInstance().scoreboardManager.scoreboardMap.values().forEach(sb -> {
                        if (sb instanceof UHCBoard) {
                            final UHCBoard uhcb = (UHCBoard) sb;
                            uhcb.updatePlayersLeft(new_aliv);
                            if (isteam) {
                                uhcb.updateTeamsLeft(teams_left);
                            }
                        }
                    });

                }
                return true;
            }

            return true;
        }
        if (cmd.getName().equalsIgnoreCase("findoff")) {
            if (!sender.hasPermission("uhc.dq")) {
                return true;
            }
            Map<OfflinePlayer, UHCPlayer> uhcOffline = new HashMap<>();
            for (UHCPlayer uhc : instance.playerManager.players.values()) {
                if (!uhc.alive)
                    continue;
                Player player = Bukkit.getPlayer(uhc.uuid);
                if (player == null || !player.isOnline()) {
                    OfflinePlayer of = Bukkit.getOfflinePlayer(uhc.uuid);
                    uhcOffline.put(of, uhc);
                }
            }
            sender.sendMessage("Players offline: ");
            uhcOffline.forEach((of, uhcp) -> {
                sender.sendMessage(" - " + of.getName());
            });

            return true;
        }
        if (cmd.getName().equalsIgnoreCase("kt"))

        {
            final Map<UUID, Integer> tm = instance.playerManager.getKT();
            final LinkedHashMap<UUID, Integer> ltm = new LinkedHashMap<>();
            tm.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10)
                    .forEachOrdered(x -> ltm.put(x.getKey(), x.getValue()));
            int i = 0;
            for (final Entry<UUID, Integer> entry : ltm.entrySet()) {
                i++;
                sender.sendMessage(i + ". " + Bukkit.getOfflinePlayer(entry.getKey()).getName() + ": "
                        + entry.getValue() + " kills");
            }

            return true;
        }
        if (cmd.getName().equalsIgnoreCase("fix")) {
            final Player player = (Player) sender;
            final Location pl = player.getLocation();
            player.teleport(pl);
            Bukkit.getScheduler().runTaskLater(instance, () -> {
                player.teleport(pl);
            }, 3L);

            return true;
        }
        return false;
    }

    public static void knockBack(final Player player, final Location loc) {
        // player -> player to knockback
        // loc -> location to knockback the player away from.
        final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1).setY(.1);
        if (player.isInsideVehicle()) {
            player.getVehicle().setVelocity(v);
            return;
        }
        player.setVelocity(v);
    }

}