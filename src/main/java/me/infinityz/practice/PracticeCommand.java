package me.infinityz.practice;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.infinityz.UHC;

/**
 * PracticeCommand
 */
public class PracticeCommand implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (UHC.getInstance().practiceManager.enabled) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (UHC.getInstance().practiceManager.isInPractice(player.getUniqueId())) {
                        UHC.getInstance().practiceManager.leavePractice(player);
                        return true;
                    }
                    UHC.getInstance().practiceManager.joinPractice(player);
                    return true;

                }
                sender.sendMessage("Console can't join/leave practice");
                return true;
            }
            sender.sendMessage("Practice is currently disabled!");

            return true;
        }
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
            case "on":
            case "true":
            case "enable": {
                sender.sendMessage(UHC.getInstance().practiceManager.enabled ? "Arena is already enabled!"
                        : "Arena has been enabled!");
                UHC.getInstance().practiceManager.enabled = true;
                Bukkit.getPluginManager().registerEvents(UHC.getInstance().practiceManager.practiceListener,
                        UHC.getInstance());
                break;
            }
            case "off":
            case "false":
            case "disable": {
                sender.sendMessage(!UHC.getInstance().practiceManager.enabled ? "Arena is already disabled!"
                        : "Arena has been disabled!");
                UHC.getInstance().practiceManager.enabled = false;
                // Handle removing all players
                UHC.getInstance().practiceManager.practiceHashSet.forEach(it -> {
                    Player player = Bukkit.getPlayer(it);
                    if (player != null && player.isOnline()) {
                        UHC.getInstance().practiceManager.leavePractice(player);
                    }
                });
                UHC.getInstance().practiceManager.practiceHashSet.clear();
                HandlerList.unregisterAll(UHC.getInstance().practiceManager.practiceListener);
                break;
            }

            }
            return true;
        }
        return false;
    }

}