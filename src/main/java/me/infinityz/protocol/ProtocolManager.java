package me.infinityz.protocol;

import java.lang.reflect.Method;

import com.google.gson.Gson;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.netty.channel.Channel;
import me.infinityz.UHC;
import me.infinityz.protocol.Reflection.FieldAccessor;
import me.infinityz.scoreboard.ScoreboardSign;
import net.minecraft.server.v1_8_R3.ChatBaseComponent;

/**
 * ProtocolManager
 */

@SuppressWarnings("unused")
public class ProtocolManager {
    private Class<?> scoreboardClass = Reflection.getClass("{nms}.PacketPlayOutScoreboardObjective");
    private FieldAccessor<String> objectiveName = Reflection.getField(scoreboardClass, String.class, 0);
    private FieldAccessor<Object> displayMode = Reflection.getField(scoreboardClass, Object.class, 2);

    private Class<?> windowData = Reflection.getClass("{nms}.PacketPlayOutWindowData");
    private FieldAccessor<Integer> othervalue = Reflection.getField(windowData, int.class, 0);
    private FieldAccessor<Integer> windowID = Reflection.getField(windowData, int.class, 1);
    private FieldAccessor<Integer> value = Reflection.getField(windowData, int.class, 2);

    private Class<?> enchantClass = Reflection.getClass("{nms}.PacketPlayInEnchantItem");
    private Class<?> experienceClass = Reflection.getClass("{nms}.PacketPlayOutExperience");

    private Class<?> entityEffectClass = Reflection.getClass("{nms}.PacketPlayOutEntityEffect");
    private FieldAccessor<Byte> effectId = Reflection.getField(entityEffectClass, byte.class, 0);

    private Class<?> soundClass = Reflection.getClass("{nms}.PacketPlayOutNamedSoundEffect");
    private FieldAccessor<String> soundName = Reflection.getField(soundClass, String.class, 0);

    private Class<?> chatOut = Reflection.getClass("{nms}.PacketPlayOutChat");
    private Class<Object> chatBase = Reflection.getUntypedClass("{nms}.IChatBaseComponent");

    private FieldAccessor<Object> chatComponentField = Reflection.getField(chatOut, chatBase, 0);

    public TinyProtocol protocol;

    public ProtocolManager(UHC instance) {

        protocol = new TinyProtocol(instance) {

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                if (chatOut.isInstance(packet)) {
                    System.out.println("packet");

                }
                return super.onPacketOutAsync(sender, channel, packet);
            }

            @Override
            public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {
                assert packet != null;
                if (scoreboardClass.isInstance(packet)) {
                    if (objectiveName.get(packet).equalsIgnoreCase("health")) {
                        Object ping = displayMode.get(packet);
                        Class<?> b = Reflection.getClass(ping.getClass().getName());
                        try {
                            Method f = b.getMethod("values");
                            ScoreboardSign.setField(packet, "c", ((Object[]) f.invoke(null))[0]);
                        } catch (Exception io) {
                            io.printStackTrace();
                        }
                    }

                } else if (entityEffectClass.isInstance(packet)
                        && !UHC.getInstance().gameConfigManager.gameConfig.absorption) {// Absorptionless on a packet
                                                                                        // level
                    byte id = effectId.get(packet);
                    if (id == (byte) 22) {
                        reciever.removePotionEffect(PotionEffectType.ABSORPTION);
                        return null;
                    }
                } else if (soundClass.isInstance(packet)) {
                    if (soundName.get(packet).equalsIgnoreCase("dig.glass")) {
                        return null;
                    }
                } else if (chatOut.isInstance(packet)) {
                    Object k = chatComponentField.get(packet);

                }

                return super.onPacketOutAsync(reciever, channel, packet);
            }

        };

    }
}