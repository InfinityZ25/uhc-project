package me.infinityz.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import me.infinityz.UHC;
import me.infinityz.events.listeners.GlobalListeners;
import me.infinityz.events.listeners.LobbyListeners;
import me.infinityz.events.listeners.SpectatorsListeners;

/**
 * ListenerManager
 */
public class ListenerManager {
    UHC instance;
    public Listener globalListener, lobbyListener, ingameListener, scatterListener, spectatorListener;

    public ListenerManager(UHC instance) {
        this.instance = instance;
        this.globalListener = new GlobalListeners(instance);
        this.lobbyListener = new LobbyListeners(instance);
        this.spectatorListener = new SpectatorsListeners(instance);
        Bukkit.getPluginManager().registerEvents(globalListener, instance);
        Bukkit.getPluginManager().registerEvents(lobbyListener, instance);
        Bukkit.getPluginManager().registerEvents(spectatorListener, instance);
    }
}