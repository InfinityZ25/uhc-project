package me.infinityz.logic;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.infinityz.player.UHCPlayer;

/**
 * GameWinEvent
 */
public class GameWinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public UHCPlayer uhcPlayer;

    public GameWinEvent(UHCPlayer e) {
        this.uhcPlayer = e;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}