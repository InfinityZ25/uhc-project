package me.infinityz.logic;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * PlayerScatteredEvent
 */
public class PlayerScatteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public Player player;

    public PlayerScatteredEvent(Player player) {
        this.player = player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}