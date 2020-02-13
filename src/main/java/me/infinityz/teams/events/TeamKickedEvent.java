package me.infinityz.teams.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.infinityz.teams.objects.Team;

/**
 * TeamLeftEvent
 */
public class TeamKickedEvent extends Event{
    private Team team;
    private Player player;
    private Player sender;
    private OfflinePlayer offline;

    public TeamKickedEvent(Team team, Player player, Player sender){
        this.team = team;
        this.player = player;
        this.sender = sender;
    }
    public TeamKickedEvent(Team team, OfflinePlayer player, Player sender){
        this.team = team;
        this.offline = player;
        this.sender = sender;
    }
    
    public Team getTeam(){
        return team;
    }

    public Player getPlayer(){
        return player;
    }

    public Player getKicker(){
        return sender;
    }
    public OfflinePlayer getOfflinePlayer(){
        return offline;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    
}