package me.infinityz.teams.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.infinityz.teams.objects.Team;

/**
 * TeamLeftEvent
 */
public class TeamRemovedEvent extends Event {
    private Team team;

    public TeamRemovedEvent(Team team) {
        this.team = team;
    }

    public Team getTeam(){
        return team;
    }
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}