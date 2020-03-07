package me.infinityz.events.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import me.infinityz.UHC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * SpectatorsListeners
 */
public class SpectatorsListeners extends SkeletonListener {
    Map<UUID, Integer> diamond;
    List<Location> mined;

    public SpectatorsListeners(UHC instance) {
        super(instance);
        diamond = new HashMap<>();
        mined = new ArrayList<>();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.isCancelled())
            return;
        switch (e.getBlock().getType()) {
            case DIAMOND_ORE: {
                if (mined.contains(e.getBlock().getLocation())) {
                    return;
                }
                mined.add(e.getBlock().getLocation());
                Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
                    int count = 1;
                    count += getNearSimilar(e.getBlock(), e.getBlock().getType());
                    String str = ChatColor.DARK_RED + "[SpecInfo] " + ChatColor.WHITE + e.getPlayer().getName()
                            + ChatColor.GRAY + " has found " + (count > 1 ? "an " : "a ") + ChatColor.WHITE + count
                            + ChatColor.GRAY + " Diamond vein!";
                    Bukkit.getOnlinePlayers().stream().filter(it -> it.hasPermission("uhc.spec")).forEach(all -> {
                        TextComponent message = new TextComponent(str);

                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(
                                        ChatColor.translateAlternateColorCodes('&', "&bTeleport to player!"))
                                                .create()));

                        message.setClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + e.getPlayer().getName()));
                        all.sendMessage(message);

                    });
                    diamond.put(e.getPlayer().getUniqueId(),
                            count + diamond.getOrDefault(e.getPlayer().getUniqueId(), 0));

                });
                break;
            }
            default:
                break;
        }
    }

    int getNearSimilar(Block block, Material type) {
        int count = 0;
        for (BlockFace face : BlockFace.values()) {
            Block b = block.getRelative(face);
            if (b.getType() == Material.DIAMOND_ORE && !mined.contains(b.getLocation())) {
                mined.add(b.getLocation());
                count++;
                count += getNearSimilar(b, type);
            }
        }
        return count;

    }

}