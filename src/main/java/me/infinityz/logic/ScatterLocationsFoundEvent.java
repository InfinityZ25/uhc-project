package me.infinityz.logic;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * PreGameStartEvent
 */
public class ScatterLocationsFoundEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private List<Location> locations;

    public ScatterLocationsFoundEvent(List<Location> locations) {
        this.locations = locations;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}