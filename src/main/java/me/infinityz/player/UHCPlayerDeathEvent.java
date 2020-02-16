package me.infinityz.player;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * UHCPlayerDeathEvent
 */
public class UHCPlayerDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public PlayerDeathEvent playerDeathEvent;
    public UHCPlayer uhcPlayer;

    public UHCPlayerDeathEvent(PlayerDeathEvent event, UHCPlayer uhcPlayer) {
        this.playerDeathEvent = event;
        this.uhcPlayer = uhcPlayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}