package me.infinityz.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.infinityz.UHC;
import me.infinityz.scenarios.IScenario;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
            formattedTime += minutes + "min";
            if (second == 0) {
                return formattedTime;
            }
            formattedTime += second + "s";
        }

        return formattedTime;
    }

    String onGreenOffRed(boolean bol) {
        if (bol) {
            return ChatColor.GREEN + "ON";
        }
        return ChatColor.RED + "OFF";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            GameConfig config = GameConfigManager.gameConfig;
            String str = "\n\n&7----------------------------------------\n&3UHC Configuration\n \n&b"
                    + (UHC.getInstance().teamManager.team_enabled ? "To" + UHC.getInstance().teamManager.team_size
                            : "FFA")
                    + " ";
            if (sender instanceof Player) {
                Player player = (Player) sender;
                TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes('&', str));
                List<IScenario> activeScenarios = UHC.getInstance().scenariosManager.getActiveScenarios();
                if (activeScenarios.isEmpty()) {
                    text.addExtra(ChatColor.AQUA + "Vanilla");
                } else {
                    Iterator<IScenario> it = activeScenarios.iterator();
                    while (it.hasNext()) {
                        IScenario scenario = it.next();
                        TextComponent t = new TextComponent(
                                ChatColor.AQUA + scenario.getClass().getSimpleName() + (it.hasNext() ? ", " : "."));
                        t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', scenario.description))
                                        .create()));
                        text.addExtra(t);
                    }
                }
                String config_part2 = ChatColor.translateAlternateColorCodes('&',
                        "\n\n&7Apple Rate: &f" + (config.apple_rate * 100D) + "% &7Flint Rate: &f"
                                + (config.flint_rate * 100D) + "% &7Shears:&f Work.\n" + "&7Final Heal: &f"
                                + formatTime(config.final_heal_time) + "&7 PvP: &f" + formatTime(config.pvp_time)
                                + "&7 Meetup Time: &f" + formatTime(config.border_time) + "\n&7Nether: "
                                + onGreenOffRed(config.nether) + "&7 Speed: ");
                text.addExtra(config_part2);
                TextComponent speed = new TextComponent(onGreenOffRed(config.speed_1));

                speed.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                                "&7I: " + onGreenOffRed(config.speed_1) + " &7II: " + onGreenOffRed(config.speed_2)))
                                        .create()));
                text.addExtra(speed);
                text.addExtra(ChatColor.GRAY + " Strength: ");
                TextComponent strength = new TextComponent(onGreenOffRed(config.strength_1));

                strength.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&7I: "
                                + onGreenOffRed(config.strength_1) + " &7II: " + onGreenOffRed(config.strength_2)))
                                        .create()));
                text.addExtra(strength);

                text.addExtra(ChatColor.GRAY + " Poison: ");
                TextComponent poison = new TextComponent(onGreenOffRed(config.poison_1));

                poison.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                                "&7I: " + onGreenOffRed(config.poison_1) + " &7II: " + onGreenOffRed(config.poison_2)))
                                        .create()));
                text.addExtra(poison);
                text.addExtra("\n" + ChatColor.GRAY + "Bedboms: " + onGreenOffRed(config.bedbombs) + ChatColor.GRAY
                        + " Books: " + onGreenOffRed(false) + ChatColor.translateAlternateColorCodes('&',
                                "\n\n&7----------------------------------------\n"));

                player.sendMessage(text);
            }

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