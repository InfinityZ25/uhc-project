package me.infinityz.commands;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;

import me.infinityz.UHC;
import me.infinityz.border.Border;
import me.infinityz.protocol.Reflection;
import me.infinityz.scatter.ScatterTask;
import me.infinityz.scatter.TeleportTask;
import net.md_5.bungee.api.ChatColor;

/**
 * GlobalCommands
 */
public class GlobalCommands implements CommandExecutor {

    private UHC instance;
    boolean es = false;
    int task_id = 0;

    public GlobalCommands(UHC instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("uhc")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bThis server is runing uhc-project version 2019.12.1.1-A"));
            // Make sure that the sender is not the console beyond this point
            assert sender instanceof Player;
            Player player = (Player) sender;
            // Obtain, if exist, the player's scoreboard.
            switch (args[0].toLowerCase()) {
            case "gen": {
                instance.scatter.locations.clear();
                instance.scatter.findLotsOfLocation(Bukkit.getWorlds().get(0), Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                break;
            }
            case "tp": {
                player.sendMessage("Teleporting you to location #" + args[1]);
                player.teleport(instance.scatter.locations.get(Integer.parseInt(args[1])));
                break;
            }
            case "border": {
                int i = Integer.parseInt(args[1]);
                int wall_size = Integer.parseInt(args[2]);
                // putWall(player.getLocation().getWorld(), i, wall_size);
                Border borderTask = new Border(player.getLocation().getWorld(), i, wall_size,
                        Integer.parseInt(args[3]));
                borderTask.runTaskTimer(instance, 0, Integer.parseInt(args[4]));
                break;
            }
            case "skeleton": {

                Skeleton skeletonCombatLogger = instance.skeleton.spawn(player);
                skeletonCombatLogger.setSkeletonType(SkeletonType.WITHER);
                skeletonCombatLogger.setCustomNameVisible(true);
                skeletonCombatLogger.setCustomName("CombatLogger " + args[1]);
                break;
            }
            case "scatter": {
                ScatterTask scatterTask = new ScatterTask(instance, player.getWorld(), Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                scatterTask.runTaskTimer(instance, 0, 5);
                break;
            }
            case "tps": {
                new TeleportTask(instance).runTask(instance);
                break;
            }
            case "practice": {
                if (instance.practiceManager.isInPractice(player.getUniqueId())) {
                    instance.practiceManager.leavePractice(player);
                    break;
                }
                instance.practiceManager.joinPractice(player);
                break;
            }
            case "tpw": {
                player.teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
                break;
            }
            case "scen": {
                instance.scoreboardManager.scenariosSet.add(args[1]);
                break;
            }
            case "scenrm": {
                
                instance.scoreboardManager.scenariosSet.remove(args[1]);
                break;
            }

            case "show_enchants":{
                
                Class<?> tableClass = Reflection.getClass("{nms}.ContainerEnchantTable");
                try {
                    Field field = tableClass.getDeclaredField("show_enchants");
                    field.setAccessible(true);
                    field.set(null, Boolean.parseBoolean(args[1]));
                    
                } catch (Exception e) {
                    //TODO: handle exception
                }
                break;
            }

            case "old_levels":{
                
                Class<?> tableClass = Reflection.getClass("{nms}.ContainerEnchantTable");
                try {
                    Field field = tableClass.getDeclaredField("old_levels");
                    field.setAccessible(true);
                    field.set(null, Boolean.parseBoolean(args[1]));
                    
                } catch (Exception e) {
                    //TODO: handle exception
                }
                break;
            }
            case "calc":{
                

                break;
            }

            }

            return true;
        }
        return false;
    }

}