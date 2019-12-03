package me.infinityz.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.infinityz.UHC;
import me.infinityz.border.Border;
import me.infinityz.combatlogger.SkeletonCombatLogger;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntitySkeleton;

/**
 * GlobalCommands
 */
public class GlobalCommands implements CommandExecutor {

    private UHC instance;
    boolean es = false;
    int task_id = 0;
    public GlobalCommands(UHC instance){
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("uhc")){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bThis server is runing uhc-project version 2019.12.1.1-A"));
            //Make sure that the sender is not the console beyond this point
            assert sender instanceof Player;
            Player player = (Player) sender;
            //Obtain, if exist, the player's scoreboard.
            switch(args[0].toLowerCase()){
                case "gen":{
                    instance.scatter.locations.clear();
                    instance.scatter.findLotsOfLocation(Bukkit.getWorlds().get(0), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    break;
                }
                case "tp":{                    
                    player.sendMessage("Teleporting you to location #" + args[1]);
                    player.teleport(instance.scatter.locations.get(Integer.parseInt(args[1])));
                    break;
                }
                case "border":{
                    int i = Integer.parseInt(args[1]);
                    int wall_size = Integer.parseInt(args[2]);
                    //putWall(player.getLocation().getWorld(), i, wall_size);
                    Border borderTask = new Border(player.getLocation().getWorld(), i, wall_size, Integer.parseInt(args[3]));
                    borderTask.runTaskTimer(instance, 0, Integer.parseInt(args[4]));
                    break;
                }
                case "combat":{

                    Villager villager = instance.nmsNpc.spawn(player.getLocation());
                    villager.setCustomNameVisible(true);
                    villager.setCustomName("Villager" + args[1]);

                    break;
                }
                case "skeleton":{

                    Skeleton skeletonCombatLogger = instance.skeleton.spawn(player);
                    skeletonCombatLogger.setSkeletonType(SkeletonType.WITHER);
                    skeletonCombatLogger.setCustomNameVisible(true);
                    skeletonCombatLogger.setCustomName("CombatLogger " + args[1]);
                    break;
                }
                case "inv":{
                    toInv(player.getInventory());
                    break;
                }

            }

            return true;
        }
        return false;
    }

    
    public void toInv(PlayerInventory inventory){
        int i = 0;
        for(org.bukkit.inventory.ItemStack item : inventory.getContents()){
            if(item == null){
                i++;
                continue;
            }
            Bukkit.broadcastMessage(i+ ". " + item.getType());
            i++;
        }
        for(org.bukkit.inventory.ItemStack item : inventory.getArmorContents()){
            if(item == null){
                i++;
                continue;
            }
            Bukkit.broadcastMessage(i+ ". Armor " + item.getType());
            i++;
        }

    }
    
}