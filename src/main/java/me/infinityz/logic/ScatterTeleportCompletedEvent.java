package me.infinityz.logic;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * PreGameStartEvent
 */
public class ScatterTeleportCompletedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public ScatterTeleportCompletedEvent() {
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}