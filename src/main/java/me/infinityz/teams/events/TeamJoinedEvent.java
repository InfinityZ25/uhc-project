package me.infinityz.teams.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.infinityz.teams.objects.Team;

/**
 * TeamLeftEvent
 */
public class TeamJoinedEvent extends Event{
    private Team team;
    private Player player;

    public TeamJoinedEvent(Team team, Player player){
        this.team = team;
        this.player = player;
    }
    
    public Team getTeam(){
        return team;
    }

    public Player getPlayer(){
        return player;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    
}