package me.infinityz.whitelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.infinityz.UHC;

/**
 * WhitelistCommand
 */
public class WhitelistCommand implements CommandExecutor, TabCompleter {
    WhitelistManager whitelistManager;
    String argumentHelp[];

    public WhitelistCommand(WhitelistManager whitelistManager) {
        this.whitelistManager = whitelistManager;
        this.argumentHelp = new String[] { "add", "remove", "clear", "on", "off" };
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1)
            return false;
        switch (args[0].toLowerCase()) {
        case "on": {
            if (!senderCanManageWhitelist(sender)) {
                return true;
            }
            whitelistManager.whitelist_enabled = true;
            Bukkit.broadcastMessage("Whitelist has been enabled!");
            return true;
        }
        case "off": {
            if (!senderCanManageWhitelist(sender)) {
                return true;
            }
            whitelistManager.whitelist_enabled = false;
            Bukkit.broadcastMessage("Whitelist has been disabled!");
            return true;
        }
        case "clear": {
            if (!senderCanManageWhitelist(sender)) {
                return true;
            }
            if (args.length >= 2 && args[1].equalsIgnoreCase("kick")) {
                whitelistManager.whitelist.forEach(it -> {
                    Player player = Bukkit.getPlayer(it);
                    if (player != null && player.isOnline() && !player.hasPermission("uhc.whitelist.bypass")) {
                        player.kickPlayer("Whitelist has been cleared and you've been kicked");
                    }
                });
            }
            whitelistManager.whitelist.clear();
            whitelistManager.whitelistorPlayers.clear();
            sender.sendMessage("Whitelist has been cleared");
            return true;
        }
        case "all": {
            if (!senderCanManageWhitelist(sender)) {
                return true;
            }
            Bukkit.getOnlinePlayers().forEach(all -> {
                whitelistManager.whitelist.add(all.getUniqueId());
            });
            sender.sendMessage("All online players have been whitelisted");
            return true;
        }
        case "remove":
        case "add": {
            if (args.length < 1)
                return false;
            return handleAddRemove(args, sender);
        }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("whitelist")) {
            if (sender.hasPermission("uhc.whitelist.manage")) {
                if (args.length == 1) {
                    final List<String> list = new ArrayList<>();
                    if (args[0].length() > 0) {
                        for (String string : argumentHelp) {
                            // Check if it matches any of the arguments available then autocomplete
                            if (string.startsWith(args[0].toLowerCase()))
                                list.add(string);
                        }
                    } else {
                        list.addAll(Arrays.asList(argumentHelp));
                    }
                    // Make sure everything is in alphabetical order
                    Collections.sort(list);
                    return list;
                } else if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
                    return Collections.singletonList("kick");
                }
            } else {
                if (args.length == 1) {
                    return Arrays.asList(new String[] { "add", "remove" });
                }
            }
        }
        return null;
    }

    boolean handleAddRemove(String[] args, CommandSender sender) {
        // I'm running this tasks async due to #getOfflinePlayer being main thread
        // heavy.
        Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {

            OfflinePlayer of = Bukkit.getOfflinePlayer(args[1]);
            if (of == null) {
                sender.sendMessage(args[1] + " does not exist!");
                return;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (sender.hasPermission("uhc.whitelist.manage")) {
                    if (whitelistManager.whitelist.add(of.getUniqueId())) {
                        sender.sendMessage(of.getName() + " has been added to the whitelist!");
                        return;
                    }
                    sender.sendMessage(of.getName() + " is already whitelisted!");
                    return;
                }
                // Check for donators whitelist
                // It can be infered that hereforth the sender can only be a player.
                final Player player = (Player) sender;
                final int allowed = getAllowedWhitelist(player);
                // Obtain the whitelistor instance
                WhitelistorPlayer whitelistorPlayer = whitelistManager.findWhitelistorByID(player.getUniqueId());
                if (whitelistorPlayer == null) {
                    // If whitelistor is null, then handle him and add him to the list
                    whitelistManager.whitelistorPlayers.add(new WhitelistorPlayer(player.getUniqueId()));
                    whitelistorPlayer = whitelistManager.findWhitelistorByID(player.getUniqueId());
                }
                if (allowed < 0) {
                    // Unlimited whitelists with a hard limit of 10
                    whitelistAsDonor(sender, whitelistorPlayer, of);
                } else {
                    List<UUID> uuidList = new ArrayList<>();
                    Arrays.asList(whitelistorPlayer.whitelisted_uuids).stream().filter(uuid -> uuid != null)
                            .forEach(uuid -> uuidList.add(uuid));
                    if (uuidList.size() < allowed) {
                        whitelistAsDonor(sender, whitelistorPlayer, of);
                        return;
                    }
                    // Max Whitelist has been reached at this point
                    sender.sendMessage("You can only whitelist " + allowed + " players!");
                }

            } else {
                // Remove case
                if (sender.hasPermission("uhc.whitelist.manage")) {
                    if (whitelistManager.whitelist.remove(of.getUniqueId())) {
                        sender.sendMessage(of.getName() + " has been removed from the whitelist!");
                        return;
                    }
                    sender.sendMessage(of.getName() + " is not whitelisted!");
                    return;
                }
                // Check for donators whitelist
                final Player player = (Player) sender;
                // Obtain the whitelistor instance
                WhitelistorPlayer whitelistorPlayer = whitelistManager.findWhitelistorByID(player.getUniqueId());
                if (whitelistorPlayer == null) {
                    // If whitelistor is null, then handle him and add him to the list
                    whitelistManager.whitelistorPlayers.add(new WhitelistorPlayer(player.getUniqueId()));
                    sender.sendMessage("You haven't whitelisted any players!");
                    return;
                }
                List<UUID> whitelistedByPlayer = new ArrayList<>();
                Arrays.asList(whitelistorPlayer.whitelisted_uuids).stream().filter(uuid -> uuid != null)
                        .forEach(uuid -> whitelistedByPlayer.add(uuid));
                if (whitelistedByPlayer.remove(of.getUniqueId())) {

                    UUID[] array = new UUID[whitelistedByPlayer.size()];

                    for (int i = 0; i < array.length; i++) {
                        array[i] = whitelistedByPlayer.get(i);
                    }
                    whitelistorPlayer.whitelisted_uuids = array;

                    if (whitelistManager.whitelist.remove(of.getUniqueId())) {
                        if (of.isOnline()) {
                            Player offlineToOnline = of.getPlayer();
                            if (!offlineToOnline.hasPermission("uhc.whitelist.bypass")) {
                                // Also check if whitelist is on?
                                Bukkit.getScheduler().runTask(UHC.getInstance(), () -> {
                                    offlineToOnline.kickPlayer(
                                            sender.getName() + " decided to remove you from the whitelist!");
                                });
                            }
                        }
                        sender.sendMessage(of.getName() + " has been removed from the whitelist!");
                        return;
                    }
                    sender.sendMessage(of.getName() + " is not in the whitelist!");
                    return;
                }
                sender.sendMessage("You can't remove " + of.getName() + " from the whitelist!");
            }

        });

        return true;
    }

    void whitelistAsDonor(CommandSender sender, WhitelistorPlayer whitelistorPlayer, OfflinePlayer target) {
        // Code reaches here it means that the player can whitelist as many player as
        // they want
        if (whitelistManager.whitelist.add(target.getUniqueId())) {
            List<UUID> list = new ArrayList<>();
            Arrays.asList(whitelistorPlayer.whitelisted_uuids).stream().filter(uuid -> uuid != null)
                    .forEach(uuid -> list.add(uuid));
            list.add(target.getUniqueId());
            UUID[] array = new UUID[list.size()];

            for (int i = 0; i < array.length; i++) {
                array[i] = list.get(i);
            }
            whitelistorPlayer.whitelisted_uuids = array;
            sender.sendMessage(target.getName() + " has been whitelisted!");
            return;
        }
        sender.sendMessage(target.getName() + " is already in the whitelist!");
        return;
    }

    boolean senderCanManageWhitelist(CommandSender sender) {
        if (!sender.hasPermission("uhc.whitelist.manage")) {
            sender.sendMessage("No permissions");
            return false;
        }
        return true;
    }

    int getAllowedWhitelist(Player player) {
        int whitelist_number = -1;
        if (!player.hasPermission("uhc.whitelist.*")) {
            for (int i = 1; i < 11; i++) {
                if (player.hasPermission("uhc.whitelist." + i)) {
                    whitelist_number = i;
                    break;
                }
                whitelist_number = 0;
            }
        }
        return whitelist_number;

    }

}