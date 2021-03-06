package me.infinityz;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.infinityz.chat.ChatManager;
import me.infinityz.combatlogger.CombatLoggerManager;
import me.infinityz.commands.CommandManager;
import me.infinityz.configuration.GameConfigManager;
import me.infinityz.events.ListenerManager;
import me.infinityz.logic.GameLogicManager;
import me.infinityz.player.PlayerManager;
import me.infinityz.practice.PracticeManager;
import me.infinityz.protocol.ProtocolManager;
import me.infinityz.scenarios.ScenariosManager;
import me.infinityz.scoreboard.FastBoard;
import me.infinityz.scoreboard.ScoreboardManager;
import me.infinityz.teams.TeamManager;
import me.infinityz.whitelist.WhitelistManager;
import me.infinityz.whitelist.objects.NoDuplicatesList;
import me.infinityz.world.WorldManager;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

/**
 * UHC
 */
public class UHC extends JavaPlugin implements Listener {

    private static UHC instance;
    public ScoreboardManager scoreboardManager;
    public ChatManager chatManager;
    public ListenerManager listenerManager;
    public CommandManager commandManager;
    public PracticeManager practiceManager;
    public CombatLoggerManager combatLoggerManager;
    public WorldManager worldManager;
    public ProtocolManager protocolManager;
    public WhitelistManager whitelistManager;
    public TeamManager teamManager;
    public ScenariosManager scenariosManager;
    public GameConfigManager gameConfigManager;
    public PlayerManager playerManager;
    public GameLogicManager gameLogicManager;
    // TODO: Move this somewhere else?
    public List<Location> locations;
    public HashMap<UUID, Integer> sitted;
    public ScheduledExecutorService executorService;
    public HashMap<String, Integer> map;

    // TODO: MOVE
    public NoDuplicatesList<Chunk> keepLoaded = new NoDuplicatesList<>();

    public static UHC getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        GameStage.stage = GameStage.LOADING;
        this.locations = new ArrayList<>();
        this.map = new HashMap<>();
        this.chatManager = new ChatManager(this);
        this.executorService = Executors.newScheduledThreadPool(4);
        this.sitted = new HashMap<>();
        this.scoreboardManager = new ScoreboardManager();
        this.whitelistManager = new WhitelistManager(this);
        this.teamManager = new TeamManager(this);
        this.practiceManager = new PracticeManager(this);
        this.listenerManager = new ListenerManager(this);
        this.commandManager = new CommandManager(this);
        this.worldManager = new WorldManager(this);
        this.protocolManager = new ProtocolManager(this);
        this.scenariosManager = new ScenariosManager();
        this.gameConfigManager = new GameConfigManager(this);
        this.playerManager = new PlayerManager(this);
        this.gameLogicManager = new GameLogicManager(this);
        this.combatLoggerManager = new CombatLoggerManager(this);
        GameStage.stage = GameStage.LOBBY;
    }

    @Override
    public void onDisable() {

    }

    public void deleteGameWorlds() {
        Bukkit.getOnlinePlayers().forEach(all -> {
            all.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        });
        final File file = new File("UHC");
        final File file2 = new File("UHC_nether");
        deleteDirectory(file);
        deleteDirectory(file2);
    }

    public void deleteDirectory(final File file) {
        try {
            Bukkit.unloadWorld(file.getName(), false);
            FileUtils.deleteDirectory(file);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public enum GameStage {
        LOADING, LOBBY, PRE_GAME, SCATTERING, IN_GAME, DEATHMATCH, DONE;

        public static GameStage stage;
    }

    public void sit(final Player p) {
        final Location l = p.getLocation();
        final EntityBat pig = new EntityBat(((CraftWorld) l.getWorld()).getHandle());

        pig.setLocation(l.getX(), l.getY() + 0.5, l.getZ(), 0, 0);
        pig.setInvisible(true);
        pig.setHealth(6);

        final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(pig);

        sitted.put(p.getUniqueId(), pig.getId());

        final PacketPlayOutAttachEntity sit = new PacketPlayOutAttachEntity(0, ((CraftPlayer) p).getHandle(), pig);

        try {
            Object entityPlayer = FastBoard.PLAYER_GET_HANDLE.invoke(p);
            Object playerConnection = FastBoard.PLAYER_CONNECTION.get(entityPlayer);
            FastBoard.SEND_PACKET.invoke(playerConnection, packet);
            FastBoard.SEND_PACKET.invoke(playerConnection, sit);
        } catch (Exception e) {

        }
    }

    public void stand(Player p) {
        final Location l = p.getLocation();

        final EntityArmorStand armostand = new EntityArmorStand(((CraftWorld) l.getWorld()).getHandle());

        armostand.setLocation(l.getX(), l.getY() + 0.5, l.getZ(), 0, 0);

        armostand.setCustomName("ArmorStand Packet\n  test");

        final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armostand);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);

    }

    public void unsit(Player p) {
        if (sitted.get(p.getUniqueId()) != null) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(sitted.get(p.getUniqueId()));
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            sitted.remove(p.getUniqueId());

        }
    }

    public static boolean isSignificantlySame(UUID uuid, UUID uuid2) {
        return uuid.getMostSignificantBits() == uuid2.getMostSignificantBits();
    }

    @SuppressWarnings("all")
    public void createHolo(Player player, String... strings) {
        de.inventivegames.hologram.Hologram hologram = de.inventivegames.hologram.HologramAPI
                .createHologram(player.getLocation().add(0.0, 2.0, 0.0), "I'm a hologram. Hey %%player%%!");
        hologram.spawn();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            stand(player);

        }, 40);
    }

}