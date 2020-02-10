package me.infinityz.scenarios.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.infinityz.scenarios.IScenario;

/**
 * ScenarioDisableEvent
 */
public class ScenarioDisabledEvent extends Event{
    private IScenario scenario;
    private Player player;

    public ScenarioDisabledEvent(IScenario scenario, Player player) {
        this.scenario = scenario;
        this.player = player;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public IScenario getScenario() {
        return this.scenario;
    }

    public Player getPlayer() {
        return this.player;
    }

    
}