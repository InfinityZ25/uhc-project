package me.infinityz.logic;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * PreGameStartEvent
 */
public class GameStartedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public GameStartedEvent() {
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}