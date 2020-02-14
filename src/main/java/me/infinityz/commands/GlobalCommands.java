package me.infinityz.commands;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.util.Vector;

import me.infinityz.UHC;
import me.infinityz.border.BedrockBorderTask;
import me.infinityz.protocol.Reflection;
import me.infinityz.scatter.ScatterTask;
import me.infinityz.scatter.TeleportTask;
import me.infinityz.scenarios.IScenario;
import me.infinityz.scenarios.events.ScenarioDisabledEvent;
import me.infinityz.scenarios.events.ScenarioEnabledEvent;
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
                BedrockBorderTask borderTask = new BedrockBorderTask(player.getLocation().getWorld(), i, wall_size,
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
            case "start": {

                break;
            }

            case "show_enchants": {

                Class<?> tableClass = Reflection.getClass("{nms}.ContainerEnchantTable");
                try {
                    Field field = tableClass.getDeclaredField("show_enchants");
                    field.setAccessible(true);
                    field.set(null, Boolean.parseBoolean(args[1]));

                } catch (Exception e) {
                }
                break;
            }

            case "old_levels": {

                Class<?> tableClass = Reflection.getClass("{nms}.ContainerEnchantTable");
                try {
                    Field field = tableClass.getDeclaredField("old_levels");
                    field.setAccessible(true);
                    field.set(null, Boolean.parseBoolean(args[1]));

                } catch (Exception e) {
                }
                break;
            }
            case "scenario": {
                IScenario scenario = instance.scenariosManager.scenarioMap.get(args[1]);
                if (scenario.enabled) {
                    scenario.disableScenario();
                    Bukkit.getPluginManager().callEvent(new ScenarioDisabledEvent(scenario, (Player) sender));
                } else {
                    scenario.enableScenario();
                    Bukkit.getPluginManager().callEvent(new ScenarioEnabledEvent(scenario, (Player) sender));
                }

                break;
            }
            case "rate": {
                switch (args[1].toLowerCase()) {
                case "apple": {
                    Bukkit.broadcastMessage("Applec rate: " + instance.gameConfigManager.gameConfig.apple_rate);
                    instance.gameConfigManager.gameConfig.apple_rate = Double.parseDouble(args[2]);
                    Bukkit.broadcastMessage("New Apple rate: " + instance.gameConfigManager.gameConfig.apple_rate);
                    break;
                }
                case "flint": {
                    Bukkit.broadcastMessage("Flint rate: " + instance.gameConfigManager.gameConfig.flint_rate);

                    instance.gameConfigManager.gameConfig.flint_rate = Double.parseDouble(args[2]);
                    Bukkit.broadcastMessage("New Flint rate: " + instance.gameConfigManager.gameConfig.flint_rate);
                    break;
                }
                }
                break;
            }
            case "velocity": {/*
                               * // Get velocity unit vector: Vector unitVector =
                               * player.getLocation().toVector().subtract(player.getLocation().toVector())
                               * .normalize(); // Set speed and push entity:
                               * player.setVelocity(unitVector.multiply(Double.parseDouble(args[1])));
                               */
                player.setVelocity(player.getLocation().getDirection().multiply(Double.parseDouble(args[1])));
                // knockBack(player, player.getLocation());
                break;
            }

            }

            return true;
        }
        return false;
    }

    public static void knockBack(final Player player, final Location loc) {
        // player -> player to knockback
        // loc -> location to knockback the player away from.
        final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1).setY(.1);
        if (player.isInsideVehicle()) {
            player.getVehicle().setVelocity(v);
            return;
        }
        player.setVelocity(v);
    }

}