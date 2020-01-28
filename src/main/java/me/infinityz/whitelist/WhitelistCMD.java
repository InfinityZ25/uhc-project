package me.infinityz.whitelist;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * WhitelistCMD
 * Important Note, the 'Magic Number' 69420 means op access.
 */
public class WhitelistCMD implements CommandExecutor {
    WhitelistManager whitelistManager;

    public WhitelistCMD(WhitelistManager whitelistManager){
        this.whitelistManager = whitelistManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("whitelist")) {
            if(args.length < 2)return true;
            if (sender instanceof Player) {
                Player player = (Player) sender;
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
                if(whitelist_number == 0){
                    //Player is not allowed to use the whitelist
                    return true;
                }
                if(whitelist_number == 69420){
                    //Player can whitelist as many players as they want
                    return true;                    
                }
                //If code reaches here, it means a player has a limited amount of whitelists. handle that
                WhitelistorPlayer whitelistorPlayer = whitelistManager.findWhitelistorByID(player.getUniqueId());
                if(whitelistorPlayer == null){
                    //Is whitelistor is null, then handle him and add him to the list
                }
                if(whitelistorPlayer.whitelisted_uuids.length < whitelist_number){
                    //Add the guy to the whitelist and to the whitelistors array.
                    //Check if the player being whitelist is actually a donor or not.
                    //Look into Luckperms to do this
                    // https://github.com/lucko/LuckPerms/wiki/Developer-API:-Usage#checking-if-a-player-is-in-a-group

                }

            }
            return true;
        }

        return false;
    }

}