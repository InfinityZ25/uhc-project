package me.infinityz.teams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * TeamCommand
 */
public class TeamCommand implements CommandExecutor, TabCompleter {

    TeamManager teamManager;
    String argumentHelp[];
    String userArgumentHelp[];

    public TeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
        this.argumentHelp = new String[] { "create", "invite", "accept", "enable", "disable", "size", "reset", "kick",
                "disband", "management", "chat", "leader", "leave" };
        this.userArgumentHelp = new String[] { "create", "invite", "accept", "kick", "disband", "chat", "leader",
                "leave" };
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        switch (args[0].toLowerCase()) {
        case "create": {
            if (!teamManager.team_management) {
                sender.sendMessage("Team management is disabled!");
                return true;
            }
            final Player player = (Player) sender;
            Team team = teamManager.findPlayersTeam(player.getUniqueId());
            if (team != null) {
                sender.sendMessage("You already have a team.");
                return true;
            }
            if (args.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                team = teamManager.createTeam(player, sb.toString().trim());
                // Memory cleanup
                sb = null;
            } else {
                team = teamManager.createTeam(player);
            }
            sender.sendMessage("You've created Team " + team.team_name + "!");
            // Maybe call a TeamCreatedEvent?
            break;
        }
        case "invite": {
            if (!teamManager.team_management) {
                sender.sendMessage("Team management is disabled!");
                return true;
            }
            final Player player = (Player) sender;
            Team team = teamManager.findPlayersTeam(player.getUniqueId());
            if (team == null) {
                // Create a team for the player
                team = teamManager.createTeam(player);
                sender.sendMessage("Creating a team for you...");
                sender.sendMessage("You've created Team " + team.team_name + "!");
            }
            if (team.team_leader != player.getUniqueId()) {
                sender.sendMessage("You're not the team leader");
                return true;
            }
            if (team.team_members.size() >= teamManager.team_size) {
                sender.sendMessage("Team size limit is " + teamManager.team_size);
                return true;
            }
            if (args.length > 1) {
                Player target = Bukkit.getPlayer(args[1]);
                if(target.getUniqueId() == player.getUniqueId()){
                    sender.sendMessage("You can't invite yourself to the team!");
                    return true;
                }
                if (target != null && target.isOnline()) {
                    Team targetsTeam = teamManager.findPlayersTeam(target.getUniqueId());
                    if (targetsTeam != null) {
                        sender.sendMessage(target.getName() + " already has a team!");
                        return true;
                    }
                    // Send invitation
                    sender.sendMessage("You've invited " + target.getName() + " to the team!");
                    team.sendTeamMessage(sender.getName() + " has invited " + target.getName() + " to the team!");
                    team.invitePlayer(player, target);
                    return true;
                }
                sender.sendMessage(args[1] + " is not online!");
                return true;
            }
            sender.sendMessage("\u00A7cCorrect usage: /team invite <Player>");

            // Maybe call a TeamInviteSentEvent?
            break;
        }
        case "join":
        case "accept": {
            if (!teamManager.team_management) {
                sender.sendMessage("Team management is disabled!");
                return true;
            }
            final Player player = (Player) sender;
            Team team = teamManager.findPlayersTeam(player.getUniqueId());
            if (team != null) {
                sender.sendMessage("You already have a team.");
                return true;
            }
            if (args.length > 1) {
                final Player inviter = Bukkit.getPlayer(args[1]);
                if (inviter != null && inviter.isOnline()) {
                    TeamInvite invite = teamManager.getInvite(player.getUniqueId(), inviter.getUniqueId());
                    if (invite == null) {
                        sender.sendMessage(inviter.getName() + " has not invited you to their team!");
                        return true;
                    }
                    if (invite.teamToJoin.team_members.size() >= teamManager.team_size) {
                        sender.sendMessage("You can't join " + invite.teamToJoin.team_name
                                + " because they already have enough team members!");
                        return true;
                    }
                    invite.teamToJoin.addMember(player.getUniqueId());
                    invite.teamToJoin.sendTeamMessage(ChatColor.GREEN + player.getName() + " has joined the team!");

                    teamManager.map.put(player.getUniqueId(), team);
                    teamManager.teamInvites.remove(invite);
                    return true;
                }
                sender.sendMessage(args[1] + " is not online!");
                return true;
            }
            sender.sendMessage("Correct usage: /team accept <Player>");

            break;
        }
        case "deny":
        case "reject": {
            if (!teamManager.team_management) {
                sender.sendMessage("Team management is disabled!");
                return true;
            }
            final Player player = (Player) sender;
            if (args.length > 1) {
                final Player inviter = Bukkit.getPlayer(args[1]);
                if (inviter != null && inviter.isOnline()) {
                    TeamInvite invite = teamManager.getInvite(player.getUniqueId(), inviter.getUniqueId());
                    if (invite == null) {
                        sender.sendMessage(inviter.getName() + " has not invited you to their team!");
                        return true;
                    }
                    sender.sendMessage(
                            "You've rejected the invitation to join Team " + invite.teamToJoin.team_name + "!");
                    invite.teamToJoin
                            .sendTeamMessage(ChatColor.RED + sender.getName() + " has rejected to join the team!");
                    teamManager.teamInvites.remove(invite);
                    return true;
                }
                sender.sendMessage(args[1] + " is not online!");
                return true;
            }
            sender.sendMessage("Correct usage: /team reject <Player>");
            break;
        }
        case "remove":
        case "kick": {
            if (!teamManager.team_management) {
                sender.sendMessage("Team management is disabled!");
                return true;
            }
            final Player player = (Player) sender;
            Team team = teamManager.findPlayersTeam(player.getUniqueId());
            if (team == null) {
                sender.sendMessage("You're not in a team");
                return true;
            }
            if (team.team_leader != player.getUniqueId()) {
                sender.sendMessage("You're not the team leader");
                return true;
            }
            if (args.length > 1) {
                Player target = Bukkit.getPlayer(args[1]);
                if(target.getUniqueId() == player.getUniqueId()){
                    sender.sendMessage("You can't kick yourself from the team!");
                    return true;
                }
                if (target != null && target.isOnline()) {
                    if (team.removeMember(target.getUniqueId())) {
                        target.sendMessage(
                                "You've been kicked from Team " + team.team_name + " by " + sender.getName());
                        team.sendTeamMessage(target.getName() + " has been kicked from the team!");
                        return true;
                    }
                    return true;
                }
                OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[1]);
                if (team.removeMember(offlineTarget.getUniqueId())) {
                    team.sendTeamMessage(offlineTarget.getName() + " has been kicked from the team!");
                    return true;
                }
                sender.sendMessage(args[1] + " is not a member of your team!");
                return true;
            }
            break;
        }
        case "leader": {
            if (!teamManager.team_management) {
                sender.sendMessage("Team management is disabled!");
                return true;
            }
            final Player player = (Player) sender;
            Team team = teamManager.findPlayersTeam(player.getUniqueId());
            if (team == null) {
                // Create a team for the player
                team = teamManager.createTeam(player);
                sender.sendMessage("Creating a team for you...");
                sender.sendMessage("You've created Team " + team.team_name + "!");
                return true;
            }
            if (team.team_leader != player.getUniqueId()) {
                sender.sendMessage("You're not the team leader");
                return true;
            }
            if (args.length > 1) {
                Player target = Bukkit.getPlayer(args[1]);
                if(target.getUniqueId() == player.getUniqueId()){
                    sender.sendMessage("You can't give yourself the leadership!");
                    return true;
                }
                if (target != null && target.isOnline()) {
                    if (team.getMember(target.getUniqueId()) != null) {
                        team.sendTeamMessage(sender.getName() + " has given the team to " + target.getName() + "!");
                        team.team_leader = target.getUniqueId();
                        return true;
                    }
                    sender.sendMessage(target.getName() + " is not a member of your team!");
                    return true;
                }
                sender.sendMessage(args[1] + " is not online!");
                return true;
            }
            sender.sendMessage("\u00A7cCorrect usage: /team invite <Player>");

            break;
        }
        case "leave": {
            if (!teamManager.team_management) {
                sender.sendMessage("Team management is disabled!");
                return true;
            }
            final Player player = (Player) sender;
            Team team = teamManager.findPlayersTeam(player.getUniqueId());
            if (team == null) {
                sender.sendMessage("You're not in a team");
                return true;
            }
            if (team.team_leader == player.getUniqueId()) {
                // You're the team leader.
                sender.sendMessage("You've left Team " + team.team_name + " without a leader!");
                team.removeMember(player.getUniqueId());
                teamManager.map.remove(player.getUniqueId());
                team.sendTeamMessage(sender.getName() + " has abandoned the team!");
                UUID uuid = team.team_members.get(new Random().nextInt(team.team_members.size()));
                team.team_leader = uuid;
                Player newLeader = Bukkit.getPlayer(uuid);
                if (newLeader != null && newLeader.isOnline()) {
                    team.sendTeamMessage(newLeader.getName() + " has been promoted to team leader!");
                    return true;
                }
                OfflinePlayer offlinePlayerLeader = Bukkit.getOfflinePlayer(uuid);
                team.sendTeamMessage(offlinePlayerLeader.getName() + " has been promoted to team leader!");
                return true;
            }
            team.removeMember(player.getUniqueId());
            team.sendTeamMessage(player.getName() + " has left the team!");
            sender.sendMessage("You've left Team " + team.team_name + "!");            
            teamManager.map.remove(player.getUniqueId());
            // PlayerLeftTeamEvent maybe?
            break;
        }
        case "disband": {
            if (!teamManager.team_management) {
                sender.sendMessage("Team management is disabled!");
                return true;
            }
            final Player player = (Player) sender;
            Team team = teamManager.findPlayersTeam(player.getUniqueId());
            if (team == null) {
                sender.sendMessage("You're not in a team");
                return true;
            }
            if (team.team_leader != player.getUniqueId()) {
                sender.sendMessage("You're not the team leader");
                return true;
            }
            sender.sendMessage("You've disbanded your team!");
            team.team_leader = null;
            team.team_members.remove(player.getUniqueId());            
            teamManager.map.remove(player.getUniqueId());
            team.sendTeamMessage("Your team has been disbanded by " + sender.getName());
            team.team_members.forEach(uuid -> teamManager.map.remove(uuid));
            team.team_members.clear();
            break;
        }
        case "true":
        case "on":
        case "enable": {
            if (!sender.hasPermission("uhc.team.manage")) {
                sender.sendMessage("No permissions!");
                return true;
            }
            if (teamManager.team_enabled) {
                sender.sendMessage("Teams are already enabled!");
                return true;
            }
            teamManager.team_enabled = true;
            teamManager.team_size = 2;
            sender.sendMessage("You've enabled teams!");
            break;
        }
        case "false":
        case "off":
        case "disable": {
            if (!sender.hasPermission("uhc.team.manage")) {
                sender.sendMessage("No permissions!");
                return true;
            }
            if (!teamManager.team_enabled) {
                sender.sendMessage("Teams are already disabled!");
                return true;
            }
            teamManager.team_enabled = false;
            teamManager.team_size = 1;
            sender.sendMessage("You've disabled teams!");
            // Teams disabled, make a call
            teamManager.teamList.forEach(team -> {
                team.sendTeamMessage("Your team was disbanded since teams have been disabled!");
            });
            teamManager.teamList.clear();
            teamManager.teamInvites.clear();
            teamManager.map.clear();
            break;
        }
        case "size": {
            if (!sender.hasPermission("uhc.team.manage")) {
                sender.sendMessage("No permissions!");
                return true;
            }
            try {
                int size = Integer.parseInt(args[1]);
                size = Math.abs(size);
                if (size == 1) {
                    size = 2;
                }
                sender.sendMessage("Team size has been set to " + size + "!");
                // Call a teamSizeChangeEvent

            } catch (Exception e) {
                sender.sendMessage("Team size has to be a number!");
            }
            break;
        }
        case "man":
        case "manage":
        case "management": {
            if (!sender.hasPermission("uhc.team.manage")) {
                sender.sendMessage("No permissions!");
                return true;
            }
            if (args.length > 1) {
                switch (args[1]) {
                case "on":
                case "true":
                case "enable": {
                    teamManager.team_management = true;
                    sender.sendMessage(teamManager.team_management ? "Team management is already enabled!"
                            : "Team management has been enabled!");
                    break;
                }
                case "off":
                case "false":
                case "disable": {
                    teamManager.team_management = false;
                    sender.sendMessage(!teamManager.team_management ? "Team management is already disabled!"
                            : "Team management has been disabled!");
                    break;
                }
                }
                return true;
            }
            teamManager.team_management = !teamManager.team_management;
            sender.sendMessage(
                    "Team management has been " + (teamManager.team_management ? "enabled" : "disabled") + "!");
            break;
        }
        case "reset": {
            if (!sender.hasPermission("uhc.team.manage")) {
                sender.sendMessage("No permissions!");
                return true;
            }
            teamManager.teamList.forEach(team -> {
                team.sendTeamMessage("Your team was disbanded due to teams being reset!");
            });
            teamManager.teamList.clear();
            teamManager.teamInvites.clear();
            teamManager.map.clear();
            break;
        }
        case "chat": {
            break;

        }
        default: {
            return false;
        }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("uhc.team.manage")) {
            if (args.length == 1) {

                List<String> list = new ArrayList<>();
                if (args[0].isEmpty()) {
                    list.addAll(Arrays.asList(argumentHelp));
                    Collections.sort(list);
                    return list;
                } else {
                    for (String string : argumentHelp) {
                        // Check if it matches any of the arguments available then autocomplete
                        if (string.startsWith(args[0].toLowerCase()))
                            list.add(string);
                    }
                    Collections.sort(list);
                    return list;
                }
            }

        } else {
            if (args.length == 1) {
                List<String> list = new ArrayList<>();
                if (args[0].isEmpty()) {
                    list.addAll(Arrays.asList(userArgumentHelp));
                    Collections.sort(list);
                    return list;
                } else {
                    for (String string : userArgumentHelp) {
                        // Check if it matches any of the arguments available then autocomplete
                        if (string.startsWith(args[0].toLowerCase()))
                            list.add(string);
                    }
                    Collections.sort(list);
                    return list;
                }

            }

        }

        return null;
    }

}