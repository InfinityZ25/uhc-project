package me.infinityz.player;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * UHCPlayerDisconnectEvent
 */
public class UHCPlayerDisconnectEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public UHCPlayer uhcPlayer;
    public PlayerQuitEvent playerQuitEvent;

    public UHCPlayerDisconnectEvent(PlayerQuitEvent event, UHCPlayer uhcPlayer) {
        this.playerQuitEvent = event;
        this.uhcPlayer = uhcPlayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
