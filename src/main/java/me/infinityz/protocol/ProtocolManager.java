package me.infinityz.protocol;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import me.infinityz.UHC;
import me.infinityz.protocol.Reflection.FieldAccessor;
import me.infinityz.scoreboard.IScoreboardSign;

/**
 * ProtocolManager
 */
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

    public TinyProtocol protocol;

    public ProtocolManager(UHC instance) {

        protocol = new TinyProtocol(instance) {

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                
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
                            IScoreboardSign.setField(packet, "c", ((Object[]) f.invoke(null))[0]);
                        } catch (Exception io) {
                            io.printStackTrace();
                        }
                    }

                } else if (windowData.isInstance(packet)) {
                        System.out.println(windowID.get(packet));
                        System.out.println(value.get(packet));
                        System.out.println(othervalue.get(packet));                        
                    
                }         

                return super.onPacketOutAsync(reciever, channel, packet);
            }

        };
        
    }
}