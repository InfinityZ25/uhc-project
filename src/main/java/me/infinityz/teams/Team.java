package me.infinityz.teams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Team
 */
public class Team {
    UUID team_leader;
    ArrayList<UUID> team_members;
    String team_name;

    //Recursive constructor to allow the usage of player instead of uuid
    public Team(Player player, String team_name){
        this(player.getUniqueId(), team_name);
    }
    //Constructor, initializes everything.
    public Team(UUID uuid, String team_name){
        this.team_leader = uuid;
        //Adds the team leader to the team members just to make it easier for later on.
        this.team_members = new ArrayList<>(Collections.singleton(uuid));
        this.team_name = team_name;
    }
    //Piece of code that returns an online Player is they're a team member. Otherwise a null.
    public Player getMember(UUID uuid) {
        while (team_members.iterator().hasNext()) {
            if (team_members.contains(uuid))
                return Bukkit.getPlayer(uuid);
        }
        return null;
    }
    //Method to send message to online members. It uses BaseComponent to allow clickables.
    public void sendTeamMessage(BaseComponent... component){
        team_members.forEach(uuid ->{
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline())return;
            player.sendMessage(component);
        });
    }
    //Recursive method to use string instead of BaseComponents
    public void sendTeamMessage(String string){
        sendTeamMessage(TextComponent.fromLegacyText(string));
    }
    //Boolean true is it can be completed, false if it can't.
    public boolean changeTeamLeader(Player player, UUID new_leader){
        if(player.getUniqueId() != team_leader)return false;
        if(!team_members.contains(new_leader)) return false;
        this.team_leader = new_leader;
        return true;
    }

    //Make the invite method?

}