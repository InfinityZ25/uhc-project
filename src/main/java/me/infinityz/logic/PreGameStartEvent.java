package me.infinityz.logic;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * PreGameStartEvent
 */
public class PreGameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private CommandSender commandSender;

    public PreGameStartEvent(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public PreGameStartEvent() {
        this.commandSender = null;
    }

    public CommandSender getSender() {
        return commandSender;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}