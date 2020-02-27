package me.infinityz.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.infinityz.UHC;
import net.md_5.bungee.api.ChatColor;

/**
 * GameConfigCMD
 */
public class GameConfigCMD implements CommandExecutor, TabCompleter {
    GameConfigManager GameConfigManager;
    String[] helpArray;

    public GameConfigCMD(GameConfigManager gameConfigManager) {
        this.GameConfigManager = gameConfigManager;
        Class<?> class2 = GameConfigManager.gameConfig.getClass();
        Field[] fields = class2.getDeclaredFields();
        List<String> list = new ArrayList<>();
        Arrays.asList(fields).forEach(field -> list.add(field.getName()));
        Collections.sort(list);

        String[] array = new String[list.size()];

        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        this.helpArray = array;
    }

    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds - hours * 3600) / 60;
        int second = (seconds - hours * 3600) - minutes * 60;

        String formattedTime = "";
        if (hours > 0) {
            if (hours < 10)
                formattedTime += "0";
            formattedTime += hours + ":";

            if (minutes < 10)
                formattedTime += "0";
            formattedTime += minutes + ":";

            if (second < 10)
                formattedTime += "0";
            formattedTime += second;
        } else {
            if (minutes < 10)
                formattedTime += "0";
            formattedTime += minutes + ":";

            if (second < 10)
                formattedTime += "0";
            formattedTime += second;
        }

        return formattedTime;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            GameConfig config = GameConfigManager.gameConfig;
            sender.sendMessage(" ");
            sender.sendMessage(" ");
            sender.sendMessage(
                    ChatColor.GRAY + "Host: " + ChatColor.WHITE + "" + GameConfigManager.last_known_host_name);
            sender.sendMessage(
                    ChatColor.GRAY + "Map Size: " + ChatColor.WHITE + "" + config.map_size + "x" + config.map_size);
            sender.sendMessage(ChatColor.GRAY + "Game Type: " + ChatColor.WHITE
                    + (UHC.getInstance().teamManager.team_enabled ? "To" + UHC.getInstance().teamManager.team_size
                            : "FFA"));
            sender.sendMessage(ChatColor.GRAY + "Scenarios: " + ChatColor.WHITE + ""
                    + (UHC.getInstance().scenariosManager.getActiveScenarios().isEmpty() ? "Vanilla"
                            : UHC.getInstance().scenariosManager.getActiveScenariosNames().toString()));
            sender.sendMessage(
                    ChatColor.GRAY + "Border Shrink Time: " + ChatColor.WHITE + formatTime(config.border_time));
            sender.sendMessage(
                    ChatColor.GRAY + "Final heal Time: " + ChatColor.WHITE + formatTime(config.final_heal_time));
            sender.sendMessage(ChatColor.GRAY + "Pvp Time: " + ChatColor.WHITE + formatTime(config.pvp_time));
            sender.sendMessage(ChatColor.GRAY + "Apple Rate: " + ChatColor.WHITE + (config.apple_rate * 100D));
            sender.sendMessage(ChatColor.GRAY + "Flint Rate: " + ChatColor.WHITE + (config.flint_rate * 100D));
            sender.sendMessage(ChatColor.GRAY + "Absorption: " + ChatColor.WHITE + config.absorption);
            sender.sendMessage(ChatColor.GRAY + "God Apples: " + ChatColor.WHITE + config.godapples);
            sender.sendMessage(ChatColor.GRAY + "Pearl Damage: " + ChatColor.WHITE + config.ender_pearl_damage);
            sender.sendMessage(ChatColor.GRAY + "Nether: " + ChatColor.WHITE + " true");
            sender.sendMessage(ChatColor.GRAY + "Strength I: " + ChatColor.WHITE + config.strength_1);
            sender.sendMessage(ChatColor.GRAY + "Strength II: " + ChatColor.WHITE + config.strength_2);
            sender.sendMessage(ChatColor.GRAY + "Speed I: " + ChatColor.WHITE + config.speed_1);
            sender.sendMessage(ChatColor.GRAY + "Speed II: " + ChatColor.WHITE + config.speed_2);
            sender.sendMessage(" ");
            sender.sendMessage(" ");

            return true;
        }
        if (args.length < 2)
            return false;
        try {
            Class<?> class1 = GameConfigManager.gameConfig.getClass();
            Field field = class1.getDeclaredField(args[0]);
            Object data;
            try {
                data = Integer.parseInt(args[1]);
            } catch (Exception e) {
                try {
                    data = Double.parseDouble(args[1]);

                } catch (Exception e2) {
                    data = Boolean.parseBoolean(args[1]);
                }

            }
            sender.sendMessage(field.getName() + " has been set to " + data.toString());
            field.set(GameConfigManager.gameConfig, data);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
        if (args.length == 1) {
            if (args[0].isEmpty()) {
                return Arrays.asList(helpArray);
            }

            final List<String> list = new ArrayList<>();
            for (String string : helpArray) {
                // Check if it matches any of the arguments available then autocomplete
                if (string.toLowerCase().startsWith(args[0].toLowerCase()))
                    list.add(string);
            }
            return list;
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "horses":
                case "horsehealing":
                case "horsearmor":
                case "nether":
                case "strength_1":
                case "strength_2":
                case "invisibility_potion":
                case "regeneration_potion":
                case "speed_1":
                case "speed_2":
                case "natural_regeneration":
                case "ender_pearl_damage":
                case "poison_1":
                case "poison_2":
                case "absorption":
                case "goldenheads":
                case "headpost":
                case "godapples": {
                    final List<String> list = new ArrayList<>(Arrays.asList(new String[] { "true", "false" }));
                    if (args[1].isEmpty()) {
                        return list;
                    }
                    for (String string : new ArrayList<>(list)) {
                        if (!string.toLowerCase().startsWith(args[1].toLowerCase()))
                            list.remove(string);
                    }
                    return list;
                }
            }

        }
        return null;
    }

}