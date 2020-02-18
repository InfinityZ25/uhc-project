package me.infinityz.player;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import me.infinityz.UHC;
import me.infinityz.teams.objects.Team;

/**
 * UHCPlayer
 */
public class UHCPlayer {
    public UUID uuid;
    public UUID[] killed_players;
    public Team team;
    public boolean alive, spectator;
    public Long died_time, last_disconnect_time;
    public ItemStack[] death_Inventory;
    public ItemStack[] armour;
    public Location death_location;
    public int entity_combatlogger_id;
    // Local Data, temporal data that does not need to be permanently stored.
    // Just gamekills for now, but later more values.
    public int game_kills;
    // Database Data, cache data from database here and don't modify it.
    // When the game officialy ends, then dump all the data to the database;

    public UHCPlayer(UUID uuid) {
        this.uuid = uuid;
        this.alive = true;
        this.spectator = false;
        this.game_kills = 0;
        UHC.getInstance().playerManager.players.put(uuid, this);
    }

    public UHCPlayer(UUID uuid, Team team) {
        this.uuid = uuid;
        this.alive = true;
        this.spectator = false;
        this.game_kills = 0;
        this.team = team;
        UHC.getInstance().playerManager.players.put(uuid, this);
    }

}