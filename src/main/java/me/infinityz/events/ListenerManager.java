package me.infinityz.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import me.infinityz.UHC;
import me.infinityz.events.listeners.GlobalListeners;
import me.infinityz.events.listeners.LobbyListeners;

/**
 * ListenerManager
 */
public class ListenerManager {
    UHC instance;
    Listener globalListener, lobbyListener;

    public ListenerManager(UHC instance) {
        this.instance = instance;
        this.globalListener = new GlobalListeners(instance);
        this.lobbyListener = new LobbyListeners(instance);
        Bukkit.getPluginManager().registerEvents(globalListener, instance);
        Bukkit.getPluginManager().registerEvents(lobbyListener, instance);
    }
}