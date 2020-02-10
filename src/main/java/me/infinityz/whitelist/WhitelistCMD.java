package me.infinityz.whitelist;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * WhitelistCMD Important Note, the 'Magic Number' 69420 means op access.
 */
public class WhitelistCMD implements CommandExecutor {
    WhitelistManager whitelistManager;

    public WhitelistCMD(WhitelistManager whitelistManager) {
        this.whitelistManager = whitelistManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("whitelist")) {
            if (args.length < 2)
                return false;
            switch(args[0].toLowerCase()){
                case "add":{                    
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    if(sender instanceof Player){
                        Player player = (Player) sender;
                        int whitelist_number = getAllowedWhitelist(player);
        
                        if (whitelist_number == 0) {
                            // Player is not allowed to use the whitelist
                            return true;
                        }
                        if (whitelist_number == 69420) {
                            // Player can whitelist as many players as they want
                            boolean ls = whitelistPlayer(offlinePlayer);
                            if (ls == false) {
                                sender.sendMessage("Could't whitelist the player " + args[1]);
                            } else {
                                sender.sendMessage(args[1] + " has been whitelisted!");
                            }
                            return true;
                        }
                        // If code reaches here, it means a player has a limited amount of whitelists.
                        // handle that
                        WhitelistorPlayer whitelistorPlayer = whitelistManager.findWhitelistorByID(player.getUniqueId());
                        if (whitelistorPlayer == null) {
                            // If whitelistor is null, then handle him and add him to the list
                            whitelistManager.whitelistorPlayers.add(new WhitelistorPlayer(player.getUniqueId()));
                        }
                        if (whitelistorPlayer.whitelisted_uuids.length < whitelist_number) {
                            // Add the guy to the whitelist and to the whitelistors array.
                            // Check if the player being whitelist is actually a donor or not.
                            // Look into Luckperms to do this
                            // https://github.com/lucko/LuckPerms/wiki/Developer-API:-Usage#checking-if-a-player-is-in-a-group
                            boolean bol = whitelistPlayer(offlinePlayer);
                            if (bol == false) {
                                sender.sendMessage("Couldn't whitelist the player " + args[1]);
                            } else {
                                sender.sendMessage(args[1] + " has been whitelisted!");
                            }
                            return true;
                        } else {
                            sender.sendMessage("You can whitelist up to " + whitelist_number + " players to the whitelist!");
                        }        

                    }else{
                        boolean bol = whitelistPlayer(offlinePlayer);
                        if (bol == false) {
                            sender.sendMessage("Couldn't whitelist the player " + args[1]);
                        } else {
                            sender.sendMessage(args[1] + " has been whitelisted!");
                        }
                    }
                    break;
                }
                case "remove":{
                    if(sender instanceof Player){

                    }else{
                        
                    }
                    break;
                }
                default:{
                    return false;
                }
            }
            return true;
        }

        return false;
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

    boolean whitelistPlayer(OfflinePlayer offlinePlayer) {
        if (!whitelistManager.whitelist.contains(offlinePlayer.getUniqueId())) {
            whitelistManager.whitelist.add(offlinePlayer.getUniqueId());
            return true;
        }
        return false;
    }

}