package me.infinityz.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.infinityz.UHC;

/**
 * ChatManager
 */
public class ChatManager {
    UHC instance;
    public Map<UUID, Long> time;
    public int chat_delay_ms;
    public boolean enabled;

    public ChatManager(UHC instance) {
        this.instance = instance;
        this.enabled = true;
        this.chat_delay_ms = 2500;
        this.time = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), instance);
    }
}