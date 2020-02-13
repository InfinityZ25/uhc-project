package me.infinityz.teams.objects;

import java.util.UUID;

/**
 * TeamInvite
 */
public class TeamInvite {
    public Team teamToJoin;
    public UUID sender, target;

    public TeamInvite(Team teamToJoin, UUID sender, UUID target){
        this.teamToJoin = teamToJoin;
        this.sender = sender;
        this.target = target;
    }
}