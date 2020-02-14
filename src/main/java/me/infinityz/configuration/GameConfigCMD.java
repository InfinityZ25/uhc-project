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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            Class<?> class2 = GameConfigManager.gameConfig.getClass();
            List<String> fields = new ArrayList<>();
            Arrays.asList(class2.getDeclaredFields()).forEach(f -> fields.add(f.getName()));
            Collections.sort(fields);
            fields.forEach(field -> {
                try {
                    Object data = class2.getDeclaredField(field).get(GameConfigManager.gameConfig);
                    sender.sendMessage(field + ": " + data.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

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