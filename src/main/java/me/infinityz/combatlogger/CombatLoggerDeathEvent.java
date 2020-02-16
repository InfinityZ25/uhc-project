package me.infinityz.combatlogger;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * CombatLoggerDeathEvent
 */
public class CombatLoggerDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public UUID skeletonCombatLogger;

    public CombatLoggerDeathEvent(UUID skeletonCombatLogger) {
        this.skeletonCombatLogger = skeletonCombatLogger;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}