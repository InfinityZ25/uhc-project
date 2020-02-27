package me.infinityz.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.WorldCreator;

import me.infinityz.UHC;
import net.md_5.bungee.api.ChatColor;

/**
 * WorldManager
 */
public class WorldManager {

    UHC instance;

    public WorldManager(UHC instance) {
        this.instance = instance;
        try {
            generateMapImage();

        } catch (Exception e) {
            // TODO: handle exception
        }
        this.createWorld("UHC", Environment.NORMAL, true);
        Bukkit.getWorld("Lobby").setGameRuleValue("doMobSpawning", "false");
        Bukkit.getWorld("Lobby").setDifficulty(Difficulty.PEACEFUL);
        this.checkMainWorlds();
        try {
            Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
            Objective obj = sb.getObjective("health");
            if (obj == null) {
                obj = sb.registerNewObjective("health", "health");
            }
            Objective obj2 = sb.getObjective("health2");
            if (obj2 == null) {
                obj2 = sb.registerNewObjective("health", "health");
            }
            obj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            obj2.setDisplaySlot(DisplaySlot.BELOW_NAME);
            obj2.setDisplayName(ChatColor.RED + "\u2764");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createWorld(String worldName, Environment environment, boolean nether) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.environment(environment);
        worldCreator.createWorld();
        if (nether) {
            WorldCreator netherWorldCreator = new WorldCreator(worldName + "_nether");
            netherWorldCreator.environment(Environment.NETHER);
            netherWorldCreator.createWorld();
        }
    }

    void checkMainWorlds() {
        World nether = Bukkit.getWorld(Bukkit.getWorlds().get(0).getName() + "_nether");
        if (nether != null)
            Bukkit.unloadWorld(nether, false);
        World end = Bukkit.getWorld(Bukkit.getWorlds().get(0).getName() + "_the_end");
        if (end != null)
            Bukkit.unloadWorld(nether, false);

    }

    void generateMapImage() {
        int random_added = new Random().nextInt(25);
        int width = 50 + random_added;
        int height = 50 + random_added;

        Boolean bol = new Random().nextBoolean();
        Color color = !bol ? new Color(141, 179, 96) : new Color(250, 148, 24);

        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics which can be used to draw into the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        // fill all the image with white
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // create a circle with black
        g2d.setColor(color);
        g2d.fillOval(0, 0, width, height);

        bufferedImage = toBufferedImage(makeColorTransparent(bufferedImage, Color.WHITE));

        // Disposes of this graphics context and releases any system resources that it
        // is using.
        g2d.dispose();

        // Save as PNG
        try {
            File file = new File("./plugins/TerrainControl/worlds/UHC/maping.png");
            ImageIO.write(bufferedImage, "png", file);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Just copy-paste this method
    Image makeColorTransparent(BufferedImage im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {

            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}