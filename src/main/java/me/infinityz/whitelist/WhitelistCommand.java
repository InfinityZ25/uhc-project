package me.infinityz.whitelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        this.argumentHelp = new String[] { "add", "remove", "clear", "on", "off"};
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length < 2) return false;
        switch(args[0].toLowerCase()){
            case "on":{
                if(sender.hasPermission("uhc.whitelist.manage")){
                    sender.sendMessage("No permissions");
                    return true;
                }
                whitelistManager.whitelist_enabled = true;
                Bukkit.broadcastMessage("Whitelist has been enabled!");

                break;
            }
            case "off":{
                if(sender.hasPermission("uhc.whitelist.manage")){
                    sender.sendMessage("No permissions");
                    return true;
                }
                whitelistManager.whitelist_enabled = false;
                Bukkit.broadcastMessage("Whitelist has been disabled!");

                break;
            }
            case "clear":{
                if(sender.hasPermission("uhc.whitelist.manage")){
                    sender.sendMessage("No permissions");
                    return true;
                }
                if(args[1].equalsIgnoreCase("kick")){
                    whitelistManager.whitelist.forEach(it ->{
                        Player player = Bukkit.getPlayer(it);
                        if(player != null && player.isOnline()){
                            player.kickPlayer("Whitelist has been cleared and you've been kicked");
                        }
                    });
                }
                whitelistManager.whitelist.clear();
                whitelistManager.whitelistorPlayers.clear();
                sender.sendMessage("Whitelist has been cleared");

                break;
            }
            case "all":{
                if(sender.hasPermission("uhc.whitelist.manage")){
                    sender.sendMessage("No permissions");
                    return true;
                }
                Bukkit.getOnlinePlayers().forEach(all ->{
                    whitelistManager.whitelist.add(all.getUniqueId());
                });
                sender.sendMessage("All online players have been whitelisted");

                break;
            }
            case "remove":
            case "add":{
                return handleAddRemove(args, sender);         
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("whitelist")){
            if(args.length == 1){
                List<String> list = new ArrayList<>(Arrays.asList(argumentHelp));

                return list;
            }
            else if(args.length == 2 && args[0].equalsIgnoreCase("clear")){
                return Collections.singletonList("kick");
            }
        }
        return null;
    }

    boolean handleAddRemove(String[] args, CommandSender sender){
        //I'm running this tasks async due to #getOfflinePlayer being main thread heavy.
        Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), ()->{
        
            OfflinePlayer of = Bukkit.getOfflinePlayer(args[1]);
            if(of == null){
                sender.sendMessage(args[1] + " does not exist!");
                return;
            }
            if(args[0].equalsIgnoreCase("add")){
                if(sender.hasPermission("uhc.whitelist.manage")){
                    if(whitelistManager.whitelist.add(of.getUniqueId())){
                        sender.sendMessage(of.getName() + " has been added to the whitelist!");
                        return;
                    }
                    sender.sendMessage(of.getName() + " is already whitelisted!");
                    return;
                }
                //Check for donators whitelist             
    
            }else{
                //Remove case
                if(sender.hasPermission("uhc.whitelist.manage")){
                    if(whitelistManager.whitelist.remove(of.getUniqueId())){
                        sender.sendMessage(of.getName() + " has been removed from the whitelist!");
                        return;
                    }
                    sender.sendMessage(of.getName() + " is not whitelisted!");
                    return;
                }
                //Check for donators whitelist
            }


        });

        return true;
    }

    
    int getAllowedWhitelist(Player player) {
        int whitelist_number = 69420;
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