package me.infinityz.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;

import me.infinityz.UHC;
import me.infinityz.teams.objects.Team;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;

/**
 * ChatListener
 */
public class ChatListener implements Listener {

    ChatManager chatManager;
    Chat vault;
    String format;

    public ChatListener(ChatManager chatManager) {
        this.chatManager = chatManager;
        this.format = "{team}{game_rank}{preffix}{name}{suffix}: %2$s";
        refreshVault();
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasPermission("uhc.override"))
            return;
        if (e.getMessage().startsWith("/me") || e.getMessage().startsWith("/bukkit")
                || e.getMessage().startsWith("/bukkit") || e.getMessage().startsWith("/minecraft")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!chatManager.enabled) {
            if (!e.getPlayer().hasPermission("uhc.chat")) {
                e.setCancelled(true);
                return;
            }
        }
        if (e.getPlayer().hasPermission("uhc.chat.nodelay"))
            return;
        final Long t = chatManager.time.get(e.getPlayer().getUniqueId());
        if (t != null && System.currentTimeMillis() < t + chatManager.chat_delay_ms) {
            double rounded_time = Math
                    .round(((System.currentTimeMillis() - t - chatManager.chat_delay_ms) / 1000D) * 10) / 10.0;
            e.getPlayer().sendMessage(
                    ChatColor.RED + "You have to wait at least " + Math.abs(rounded_time) + "s to talk again");
            e.setCancelled(true);
            return;
        }
        chatManager.time.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());

    }

    @EventHandler
    public void onServiceChange(ServiceRegisterEvent e) {
        if (e.getProvider().getService() == Chat.class) {
            refreshVault();
        }
    }

    @EventHandler
    public void onServiceChange(ServiceUnregisterEvent e) {
        if (e.getProvider().getService() == Chat.class) {
            refreshVault();
        }
    }

    private void refreshVault() {
        Chat vaultChat = chatManager.instance.getServer().getServicesManager().load(Chat.class);
        if (vaultChat != this.vault) {
            chatManager.instance.getLogger().info(
                    "New Vault Chat implementation registered: " + (vaultChat == null ? "null" : vaultChat.getName()));
        }
        this.vault = vaultChat;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatLow(AsyncPlayerChatEvent e) {
        e.setFormat(format);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatHigh(AsyncPlayerChatEvent e) {
        if (e.isCancelled())
            return;
        String format = e.getFormat();
        if (vault != null && format.contains("{preffix}")) {
            format = format.replace("{preffix}", colorize(vault.getPlayerPrefix(e.getPlayer())));
        }
        if (vault != null && format.contains("{suffix}")) {
            format = format.replace("{suffix}", colorize(vault.getPlayerSuffix(e.getPlayer())));
        }
        if (format.contains("{team}")) {
            if (chatManager.instance.teamManager.team_enabled) {
                final Team t = chatManager.instance.teamManager.findPlayersTeam(e.getPlayer().getUniqueId());
                format = format.replace("{team}", t != null ? colorize("[Team " + t.team_name + "] ") : "");
            }
            format = format.replace("{team}", "");
        }
        format = format.replace("{game_rank}",
                chatManager.instance.gameConfigManager.host != null && UHC
                        .isSignificantlySame(e.getPlayer().getUniqueId(), chatManager.instance.gameConfigManager.host)
                                ? colorize("&4[Host]&r")
                                : "");
        format = format.replace("{name}", e.getPlayer().getName());
        e.setFormat(format);
    }

    private static String colorize(String s) {
        return s == null ? null : ChatColor.translateAlternateColorCodes('&', s);
    }

}