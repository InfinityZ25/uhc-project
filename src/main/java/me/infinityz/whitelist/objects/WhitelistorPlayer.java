package me.infinityz.whitelist.objects;

import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;

/**
 * WhitelistedPlayer
 */
public class WhitelistorPlayer {
    public UUID player_uuid;
    public UUID[] whitelisted_uuids;

    public WhitelistorPlayer(UUID player_uuid, UUID whitelited_uuid){
        this.player_uuid = player_uuid;
        this.whitelisted_uuids = new UUID[10];
        ArrayUtils.add(this.whitelisted_uuids, whitelited_uuid);
    }

    public WhitelistorPlayer(UUID player_uuid){
        this.player_uuid = player_uuid;
        this.whitelisted_uuids = new UUID[10];
    }
}