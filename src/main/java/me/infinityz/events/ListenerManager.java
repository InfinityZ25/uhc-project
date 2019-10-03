package me.infinityz.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import me.infinityz.UHC;
import me.infinityz.events.listeners.GlobalListeners;

/**
 * ListenerManager
 */
public class ListenerManager {
    UHC instance;
    Listener globalListener, lobbyListener;
    
    public ListenerManager(UHC instance) {
        this.instance = instance;
        this.globalListener = new GlobalListeners(instance);
        Bukkit.getPluginManager().registerEvents(globalListener, instance);
    }
}