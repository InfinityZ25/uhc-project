package me.infinityz.whitelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.infinityz.UHC;
import me.infinityz.whitelist.objects.NoDuplicatesList;
import me.infinityz.whitelist.objects.WhitelistorPlayer;

/**
 * WhitelistManager
 */
public class WhitelistManager {
    public boolean whitelist_enabled;
    public NoDuplicatesList<UUID> whitelist;
    public List<WhitelistorPlayer> whitelistorPlayers;

    public WhitelistManager(UHC instance) {
        whitelist = new NoDuplicatesList<>();
        whitelist_enabled = true;
        whitelistorPlayers = new ArrayList<>();
        WhitelistCommand whitelistCommand = new WhitelistCommand(this);
        instance.getCommand("whitelist").setExecutor(whitelistCommand);
        instance.getCommand("whitelist").setTabCompleter(whitelistCommand);
        Bukkit.getPluginManager().registerEvents(new WhitelistEvents(this), instance);
    }

    // Method to get a whitelistor by their UUID. Revise it later, there might be a
    // better way of doing it.
    public WhitelistorPlayer findWhitelistorByID(UUID uuid) {
        for (WhitelistorPlayer whitelistor : whitelistorPlayers) {
            if (whitelistor.player_uuid.equals(uuid))
                return whitelistor;
        }
        return null;
    }

    public WhitelistorPlayer findWhoWhoWhitelisted(UUID uuid) {
        for (WhitelistorPlayer whitelistor : whitelistorPlayers) {
            for (UUID all : Arrays.asList(whitelistor.whitelisted_uuids)) {
                if (all == null)
                    continue;
                if (all.equals(uuid))
                    return whitelistor;
            }
        }
        return null;
    }

}