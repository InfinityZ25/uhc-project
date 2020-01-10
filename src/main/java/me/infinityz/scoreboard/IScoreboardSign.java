package me.infinityz.scoreboard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;

/**
 * @author zyuiop Editted by InfinityZ25
 */
public class IScoreboardSign {
	private boolean created = false;
	private final VirtualTeam[] lines = new VirtualTeam[15];
	private final Player player;
	private String objectiveName;
	@Getter
	LinkedHashSet<UpdateObject> updateHashSet;

	/**
	 * Create a scoreboard sign for a given player and using a specifig objective
	 * name
	 * 
	 * @param player        the player viewing the scoreboard sign
	 * @param objectiveName the name of the scoreboard sign (displayed at the top of
	 *                      the scoreboard)
	 */
	public IScoreboardSign(final Player player, final String objectiveName) {
		this.player = player;
		this.objectiveName = ChatColor.translateAlternateColorCodes('&', objectiveName);
		this.updateHashSet = new LinkedHashSet<>();
	}

	/**
	 * Send the initial creation packets for this scoreboard sign. Must be called at
	 * least once.
	 */
	public void create() {
		if (created)
			return;
		final PlayerConnection player = getPlayer();
		player.sendPacket(createObjectivePacket(0, objectiveName));
		player.sendPacket(setObjectiveSlot());
		int i = 0;
		while (i < lines.length)
			sendLine(i++);

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
		for (final VirtualTeam team : lines)
			if (team != null)
				getPlayer().sendPacket(team.removeTeam());
		created = false;
	}

	/*
	 * Use this method to send a future that has to be eventually updated by a
	 * data-watcher for the global scoreboards.
	 */
	public void queueUpdate(int i, String line) {
		//Call from outside with super as a prefix.
		updateHashSet.add(new UpdateObject(line, i));
	}

	/*Update method, gets called automatically by the scoreboard manager. 
	 *Every scoreboard has to call this method at least once.
	 *Never forget to call it. I'm begging you lol.
	 */
	public void update() {
		//Obtain a iteratable object from the organized HashSet
		Iterator<UpdateObject> iterator = updateHashSet.iterator();
		//Loop through the iterable with a while loop for as long as there is something in #hasNext().
		while (iterator.hasNext()) {
			//Get the updateObject as final to ensure it is unmutable and assert it's null safe.
			final UpdateObject object = iterator.next();
			assert object != null;
			//Remove it from the queue before trying to send it so that it gets send regarless of content.	
			updateHashSet.remove(object);
			//Finally call the recursive method to send the packet.
			queueSetLine(object.line_id, object.line);
			object.destroy();
		}
	}
	//Method that allows an update to happen instantly instead of getting queued for update in the next loop cycle.
	public void forceUpdate(int line_id, String line) {
		setLine(line_id, line);
	}
	//Inner class that witholds all necesary data to 
	class UpdateObject {
		@Getter
		String line;
		@Getter
		int line_id;
		//The main method. Easy to create and understand
		public UpdateObject(String line, int line_id) {
			this.line = line;
			this.line_id = line_id;
		}
		//Destroy method to help out the garbage collector a little bit.
		public void destroy() {
			this.line = null;
			this.line_id = -1;
		}		
	}
	/**
	 * Change the name of the objective. The name is displayed at the top of the
	 * scoreboard.
	 * 
	 * @param name the name of the objective, max 32 char
	 */
	public void setObjectiveName(final String name) {
		this.objectiveName = ChatColor.translateAlternateColorCodes('&', name);
		if (created)
			getPlayer().sendPacket(createObjectivePacket(2, this.objectiveName));
	}

	/**
	 * Change a scoreboard line and send the packets to the player. Can be called
	 * async.
	 * 
	 * @param line  the number of the line (0 <= line < 15)
	 * @param value the new value for the scoreboard line
	 */
	public void setLine(final int line, final String value) {
		final VirtualTeam team = getOrCreateTeam(line);
		final String old = team.getCurrentPlayer();

		if (old != null && created)
			getPlayer().sendPacket(removeLine(old));

		team.setValue(ChatColor.translateAlternateColorCodes('&', value));
		sendLine(line);
	}

	public void queueSetLine(final int line, final String value) {
		final VirtualTeam team = getOrCreateTeam(line);
		final String old = team.getCurrentPlayer();
		List<Packet> packets = new ArrayList<>();

		if (old != null && created) {
			packets.add(removeLine(old));			
		}
		
		team.setValue(ChatColor.translateAlternateColorCodes('&', value));
		packets.addAll(queueSendLine(line));
		packets.forEach((it) -> getPlayer().sendPacket(it));
		
	}

	/**
	 * Remove a given scoreboard line
	 * 
	 * @param line the line to remove
	 */
	public void removeLine(final int line) {
		final VirtualTeam team = getOrCreateTeam(line);
		final String old = team.getCurrentPlayer();

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
	public String getLine(final int line) {
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
	public VirtualTeam getTeam(final int line) {
		if (line > 14 || line < 0)
			return null;
		return getOrCreateTeam(line);
	}

	private PlayerConnection getPlayer() {
		return ((CraftPlayer) player).getHandle().playerConnection;
	}

	private void sendLine(final int line) {
		if (line > 14 || line < 0 || !created)
			return;

		final VirtualTeam virtualTeam = getOrCreateTeam(line);

		virtualTeam.sendLine().forEach((it) -> getPlayer().sendPacket(it));

		getPlayer().sendPacket(sendScore(virtualTeam.getCurrentPlayer(), line));
		virtualTeam.reset();
	}

	public List<Packet> queueSendLine(final int line) {
		if (line > 14 || line < 0 || !created)
			return null;

		List<Packet> packet = new ArrayList<>();
		final VirtualTeam virtualTeam = getOrCreateTeam(line);

		virtualTeam.sendLine().forEach((it) -> packet.add(it));
		packet.add(sendScore(virtualTeam.getCurrentPlayer(), line));

		virtualTeam.reset();

		return packet;
	}

	private VirtualTeam getOrCreateTeam(final int line) {
		if (lines[line] == null)
			lines[line] = new VirtualTeam("__fakeScore" + line);

		return lines[line];
	}

	// Factories
	private PacketPlayOutScoreboardObjective createObjectivePacket(final int mode, final String displayName) {
		final PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
		// Objective's name
		setField(packet, "a", player.getName());

		// Mode
		// 0 : Create
		// 1 : Remove
		// 2 : Add a player
		setField(packet, "d", mode);

		if (mode == 0 || mode == 2) {
			setField(packet, "b", displayName);
			setField(packet, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
		}

		return packet;
	}

	private PacketPlayOutScoreboardDisplayObjective setObjectiveSlot() {
		final PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();
		// Slot
		setField(packet, "a", 1);
		setField(packet, "b", player.getName());

		return packet;
	}

	private PacketPlayOutScoreboardScore sendScore(final String line, final int score) {
		final PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(line);
		setField(packet, "b", player.getName());
		setField(packet, "c", score);
		setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);

		return packet;
	}

	private PacketPlayOutScoreboardScore removeLine(final String line) {
		return new PacketPlayOutScoreboardScore(line);
	}

	// Recursive method just to make the spacers.
	public String toSpaceString(int i) {
		final StringBuffer stringBuffer = new StringBuffer();
		while (i > 0) {
			stringBuffer.append("§r");
			i--;
		}
		return stringBuffer.toString();
	}

	/**
	 * This class is used to manage the content of a line. Advanced users can use it
	 * as they want, but they are encouraged to read and understand the code before
	 * doing so. Use these methods at your own risk.
	 */
	public class VirtualTeam {
		private final String name;
		private String prefix, suffix, currentPlayer, oldPlayer;
		private boolean prefixChanged, suffixChanged, playerChanged = false;
		private boolean first = true;

		private VirtualTeam(final String name, final String prefix, final String suffix) {
			this.name = name;
			this.prefix = prefix;
			this.suffix = suffix;
		}

		private VirtualTeam(final String name) {
			this(name, "", "");
		}

		public String getName() {
			return name;
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(final String prefix) {
			if (this.prefix == null || !this.prefix.equals(prefix))
				this.prefixChanged = true;
			this.prefix = prefix;
		}

		public String getSuffix() {
			return suffix;
		}

		public void setSuffix(final String suffix) {
			if (this.suffix == null || !this.suffix.equals(prefix))
				this.suffixChanged = true;
			this.suffix = suffix;
		}

		private PacketPlayOutScoreboardTeam createPacket(final int mode) {
			final PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
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
			final PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
			setField(packet, "a", name);
			setField(packet, "h", 1);
			first = true;
			return packet;
		}

		public void setPlayer(final String name) {
			if (this.currentPlayer == null || !this.currentPlayer.equals(name))
				this.playerChanged = true;
			this.oldPlayer = this.currentPlayer;
			this.currentPlayer = name;
		}

		public Iterable<PacketPlayOutScoreboardTeam> sendLine() {
			final List<PacketPlayOutScoreboardTeam> packets = new ArrayList<>();

			if (first) {
				packets.add(createTeam());
			} else if (prefixChanged || suffixChanged) {
				packets.add(updateTeam());
			}

			if (first || playerChanged) {
				if (oldPlayer != null)
					packets.add(addOrRemovePlayer(4, oldPlayer));
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
		public PacketPlayOutScoreboardTeam addOrRemovePlayer(final int mode, final String playerName) {
			final PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
			setField(packet, "a", name);
			setField(packet, "h", mode);

			try {
				final Field f = packet.getClass().getDeclaredField("g");
				f.setAccessible(true);
				((List<String>) f.get(packet)).add(playerName);
			} catch (final Exception e) {
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

		public void setValue(final String value) {
			if (value.length() <= 16) {
				setPrefix("");
				setSuffix("");
				setPlayer(value);
			} else if (value.length() <= 32) {
				setPrefix(value.substring(0, 16));
				setPlayer(value.substring(16));
				setSuffix("");
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

	public static void setField(final Object edit, final String fieldName, final Object value) {
		try {
			final Field field = edit.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(edit, value);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}