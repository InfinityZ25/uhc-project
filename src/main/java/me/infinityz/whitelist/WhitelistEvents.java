package me.infinityz.whitelist;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

/**
 * WhitelistEvents
 */
public class WhitelistEvents  implements Listener{
    WhitelistManager whitelistManager;

    public WhitelistEvents(WhitelistManager whitelistManager){
        this.whitelistManager = whitelistManager;
    }
    
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if(!whitelistManager.whitelist_enabled)return;
        final Player player = event.getPlayer();
        if (whitelistManager.whitelist.contains(player.getUniqueId()) || player.hasPermission("uhc.whitelist.bypass"))
            event.allow();
        event.disallow(Result.KICK_WHITELIST, "You're not in the whitelist! Buy a rank bud!");

    }

    
}