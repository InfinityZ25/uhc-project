package me.infinityz.whitelist;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.infinityz.UHC;

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
        instance.getCommand("whitelist").setExecutor(new WhitelistCommand(this));
        instance.getCommand("whitelist").setTabCompleter(new WhitelistCommand(this));
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

}