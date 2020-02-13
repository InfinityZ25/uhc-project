package me.infinityz.whitelist;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import me.infinityz.UHC;
import me.infinityz.whitelist.objects.WhitelistorPlayer;

/**
 * WhitelistEvents
 */
public class WhitelistEvents implements Listener {
    WhitelistManager whitelistManager;

    public WhitelistEvents(WhitelistManager whitelistManager) {
        this.whitelistManager = whitelistManager;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (!whitelistManager.whitelist_enabled)
            return;
        final Player player = event.getPlayer();
        if (player.hasPermission("uhc.whitelist.bypass")) {
            event.allow();
            return;
        }
        if (whitelistManager.whitelist.contains(player.getUniqueId())) {
            event.allow();
            Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
                WhitelistorPlayer whitelistorPlayer = whitelistManager.findWhoWhoWhitelisted(player.getUniqueId());
                if (whitelistorPlayer != null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(whitelistorPlayer.player_uuid);
                    player.sendMessage("You have been whitelisted by " + offlinePlayer.getName() + "!");
                }
            });
            return;
        }
        event.disallow(Result.KICK_WHITELIST, "You're not in the whitelist! Buy a rank bud!");

    }

}