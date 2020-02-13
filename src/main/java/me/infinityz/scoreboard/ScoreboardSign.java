package me.infinityz.scoreboard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;

/**
 * @author zyuiop
 */
public class ScoreboardSign {
    private boolean created = false;
    private final VirtualTeam[] lines = new VirtualTeam[15];

    private final Player player;
    private String objectiveName;
    String chars = "0123456789abcdef";
    int line_count = 0;

    /**
     * Create a scoreboard sign for a given player and using a specifig objective
     * name
     * 
     * @param player        the player viewing the scoreboard sign
     * @param objectiveName the name of the scoreboard sign (displayed at the top of
     *                      the scoreboard)
     */
    public ScoreboardSign(Player player, String objectiveName) {
        this.player = player;
        this.objectiveName = ChatColor.translateAlternateColorCodes('&', objectiveName);
    }

    /**
     * Send the initial creation packets for this scoreboard sign. Must be called at
     * least once.
     */
    public void create() {
        if (created)
            return;

        PlayerConnection player = getPlayer();
        player.sendPacket(createObjectivePacket(0, objectiveName));
        player.sendPacket(setObjectiveSlot());
        int i = 0;
        while (i < lines.length)
            sendLine(i++);
        //Handle this later, having AAAA team makes it appear at the top of the tablist instead of at a random spot.
        getPlayer().sendPacket(createTeam("0000", "&a"));
        getPlayer().sendPacket(createTeam("1111", "&c"));

        getPlayer().sendPacket(add3Remove4(3, player.player.getName(), "0000"));
        Bukkit.getOnlinePlayers().forEach(it -> {
            if (it.getName().equalsIgnoreCase(player.player.getName()))
                return;

            getPlayer().sendPacket(add3Remove4(3, it.getName(), "1111"));

        });
        created = true;
    }

    /**
     * Send the packets to remove this scoreboard sign. A destroyed scoreboard sign
     * must be recreated using {@link ScoreboardSign#create()} in order to be used
     * again
     */
    public void destroy() {
        if (!created)
            return;

        getPlayer().sendPacket(createObjectivePacket(1, null));
        for (VirtualTeam team : lines)
            if (team != null)
                getPlayer().sendPacket(team.removeTeam());

        created = false;
    }

    /**
     * Change the name of the objective. The name is displayed at the top of the
     * scoreboard.
     * 
     * @param name the name of the objective, max 32 char
     */
    public void setObjectiveName(String name) {
        this.objectiveName = ChatColor.translateAlternateColorCodes('&', name);
        if (created)
            getPlayer().sendPacket(createObjectivePacket(2, name));
    }

    public void updatePlayerOrder() {
        
        Bukkit.getOnlinePlayers().forEach(it -> {
            if (it.getName().equalsIgnoreCase(player.getName()))
                return;

            getPlayer().sendPacket(
                    new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) it).getHandle()));

            getPlayer().sendPacket(
                    new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) it).getHandle()));
            getPlayer().sendPacket(add3Remove4(3, it.getName(), "1111"));

        });
    }
    /**
     * Change a scoreboard line and send the packets to the player. Can be called
     * async.
     * 
     * @param line  the number of the line (0 <= line < 15)
     * @param value the new value for the scoreboard line
     */
    public void setLine(int line, String value) {
        VirtualTeam team = getOrCreateTeam(line);
        String old = team.getCurrentPlayer();

        if (old != null && created) {
            if (value.length() <= 30) {
                team.updateValue(ChatColor.translateAlternateColorCodes('&', value));
                sendLine(line);
                return;
            } else {
                getPlayer().sendPacket(removeLine(old));
                line_count--;
            }
        }

        team.setValue(ChatColor.translateAlternateColorCodes('&', value));
        sendLine(line);
    }

    /**
     * Remove a given scoreboard line
     * 
     * @param line the line to remove
     */
    public void removeLine(int line) {
        VirtualTeam team = getOrCreateTeam(line);
        String old = team.getCurrentPlayer();

        if (old != null && created) {
            getPlayer().sendPacket(removeLine(old));
            getPlayer().sendPacket(team.removeTeam());
        }

        lines[line] = null;
    }

    /**
     * Get the current value for a line
     * 
     * @param line the line
     * @return the content of the line
     */
    public String getLine(int line) {
        if (line > 14)
            return null;
        if (line < 0)
            return null;
        return getOrCreateTeam(line).getValue();
    }

    /**
     * Get the team assigned to a line
     * 
     * @return the {@link VirtualTeam} used to display this line
     */
    public VirtualTeam getTeam(int line) {
        if (line > 14)
            return null;
        if (line < 0)
            return null;
        return getOrCreateTeam(line);
    }

    public PlayerConnection getPlayer() {
        return ((CraftPlayer) player).getHandle().playerConnection;
    }

    private void sendLine(int line) {
        if (line > 14)
            return;
        if (line < 0)
            return;
        if (!created)
            return;
        VirtualTeam val = getOrCreateTeam(line);
        val.sendLine().forEach((it)-> getPlayer().sendPacket(it));
        getPlayer().sendPacket(sendScore(val.getCurrentPlayer(), line));
        val.reset();
    }

    private VirtualTeam getOrCreateTeam(int line) {
        if (lines[line] == null)
            lines[line] = new VirtualTeam("__fakeScore" + line);

        return lines[line];
    }

    /*
     * Factories
     */
    private PacketPlayOutScoreboardObjective createObjectivePacket(int mode, String displayName) {
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
        // Nom de l'objectif
        setField(packet, "a", player.getName());

        // Mode
        // 0 : créer
        // 1 : Supprimer
        // 2 : Mettre à jour
        setField(packet, "d", mode);

        if (mode == 0 || mode == 2) {
            setField(packet, "b", displayName);
            setField(packet, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        }

        return packet;
    }

    private PacketPlayOutScoreboardDisplayObjective setObjectiveSlot() {
        PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();
        // Slot
        setField(packet, "a", 1);
        setField(packet, "b", player.getName());

        return packet;
    }

    private PacketPlayOutScoreboardScore sendScore(String line, int score) {
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(line);
        setField(packet, "b", player.getName());
        setField(packet, "c", score);
        setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);

        return packet;
    }

    private PacketPlayOutScoreboardTeam createTeam(String teamName, String prefix) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        setField(packet, "a", teamName);
        setField(packet, "h", 0);
        setField(packet, "b", "");
        setField(packet, "c", ChatColor.translateAlternateColorCodes('&', prefix));
        setField(packet, "d", ChatColor.translateAlternateColorCodes('&', "&r"));
        setField(packet, "i", 0);
        setField(packet, "e", "always");
        setField(packet, "f", 0);

        return packet;
    }
    @SuppressWarnings("unchecked")
    public PacketPlayOutScoreboardTeam add3Remove4(int mode, String playerName, String teamName) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        setField(packet, "a", teamName);
        setField(packet, "h", mode);

        try {
            Field f = packet.getClass().getDeclaredField("g");
            f.setAccessible(true);
            ((List<String>) f.get(packet)).add(playerName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    private PacketPlayOutScoreboardScore removeLine(String line) {
        return new PacketPlayOutScoreboardScore(line);
    }

    /**
     * This class is used to manage the content of a line. Advanced users can use it
     * as they want, but they are encouraged to read and understand the code before
     * doing so. Use these methods at your own risk.
     */
    public class VirtualTeam {
        private final String name;
        private String prefix;
        private String suffix;
        private String currentPlayer;
        private String oldPlayer;

        private boolean prefixChanged, suffixChanged, playerChanged = false;
        private boolean first = true;

        private VirtualTeam(String name, String prefix, String suffix) {
            this.name = name;
            this.prefix = prefix;
            this.suffix = suffix;
        }

        private VirtualTeam(String name) {
            this(name, "", "");
        }

        public String getName() {
            return name;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            if (this.prefix == null || !this.prefix.equals(prefix))
                this.prefixChanged = true;
            this.prefix = prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            if (this.suffix == null || !this.suffix.equals(prefix))
                this.suffixChanged = true;
            this.suffix = suffix;
        }

        private PacketPlayOutScoreboardTeam createPacket(int mode) {
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            setField(packet, "a", name);
            setField(packet, "h", mode);
            setField(packet, "b", "");
            setField(packet, "c", prefix);
            setField(packet, "d", suffix);
            setField(packet, "i", 0);
            setField(packet, "e", "always");
            setField(packet, "f", 0);

            return packet;
        }

        public PacketPlayOutScoreboardTeam createTeam() {
            return createPacket(0);
        }

        public PacketPlayOutScoreboardTeam updateTeam() {
            return createPacket(2);
        }

        public PacketPlayOutScoreboardTeam removeTeam() {
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            setField(packet, "a", name);
            setField(packet, "h", 1);
            first = true;
            return packet;
        }

        public void setPlayer(String name) {
            if (this.currentPlayer == null || !this.currentPlayer.equals(name))
                this.playerChanged = true;
            this.oldPlayer = this.currentPlayer;
            this.currentPlayer = name;
        }

        public Iterable<PacketPlayOutScoreboardTeam> sendLine() {
            List<PacketPlayOutScoreboardTeam> packets = new ArrayList<>();

            if (first) {
                packets.add(createTeam());
            } else if (prefixChanged || suffixChanged) {
                packets.add(updateTeam());
            }

            if (first || playerChanged) {
                if (oldPlayer != null) // remove these two lines ?
                    packets.add(addOrRemovePlayer(4, oldPlayer)); //
                packets.add(changePlayer());
            }

            if (first)
                first = false;

            return packets;
        }

        public void reset() {
            prefixChanged = false;
            suffixChanged = false;
            playerChanged = false;
            oldPlayer = null;
        }

        public PacketPlayOutScoreboardTeam changePlayer() {
            return addOrRemovePlayer(3, currentPlayer);
        }

        @SuppressWarnings("unchecked")
        public PacketPlayOutScoreboardTeam addOrRemovePlayer(int mode, String playerName) {
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            setField(packet, "a", name);
            setField(packet, "h", mode);

            try {
                Field f = packet.getClass().getDeclaredField("g");
                f.setAccessible(true);
                ((List<String>) f.get(packet)).add(playerName);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return packet;
        }

        public String getCurrentPlayer() {
            return currentPlayer;
        }

        public String getValue() {
            return getPrefix() + getCurrentPlayer() + getSuffix();
        }

        String get(String str) {
            String stt = "";

            return stt;
        }

        public void setValue(String value) {
            if (value.length() <= 16) {
                setPrefix(value);
                setPlayer("§" + chars.charAt(line_count));
                setSuffix("");
            } else if (value.length() <= 30) {
                String st[] = value.split("§");
                if (st.length > 0) {                    
                    setPrefix(value.substring(0, 16));
                    setPlayer("§" + chars.charAt(line_count));
                    setSuffix("§" + st[st.length - 1].charAt(1) + value.substring(16));
                } else {
                    setPrefix(value.substring(0, 16));
                    setPlayer("§" + chars.charAt(line_count));
                    setSuffix(value.substring(16));
                }
            } else if (value.length() <= 48) {
                setPrefix(value.substring(0, 16));
                setPlayer(value.substring(16, 32));
                setSuffix(value.substring(32));
            } else {
                throw new IllegalArgumentException(
                        "Too long value ! Max 48 characters, value was " + value.length() + " !");
            }
            line_count++;
        }

        public void updateValue(String value) {
            if (value.length() <= 16) {
                setPrefix(value);
                setSuffix("");
            } else if (value.length() <= 30) {
                String st[] = value.split("§");
                if (st.length > 0) {
                    setPrefix(value.substring(0, 16));
                    prefixChanged = true;
                    setSuffix("§" + st[st.length - 1].charAt(0) + value.substring(16));
                } else {
                    prefixChanged = true;
                    suffixChanged = true;
                    setPrefix(value.substring(0, 16));
                    setSuffix(value.substring(16));
                }
            } else if (value.length() <= 48) {
                setPrefix(value.substring(0, 16));
                setPlayer(value.substring(16, 32));
                setSuffix(value.substring(32));
            } else {
                throw new IllegalArgumentException(
                        "Too long value ! Max 48 characters, value was " + value.length() + " !");
            }
        }
    }

    public void setField(Object edit, String fieldName, Object value) {
        try {
            Field field = edit.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(edit, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}