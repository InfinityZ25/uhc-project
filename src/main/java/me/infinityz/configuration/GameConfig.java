package me.infinityz.configuration;

/**
 * GameConfig
 */
public class GameConfig {

    public int map_size, final_heal_time, pvp_time, border_time, max_players;
    public double apple_rate, flint_rate;
    public boolean nether, bedbombs, strength_1, strength_2, speed_1, speed_2, poison_1, poison_2, invisibility_potion,
            regeneration_potion, ender_pearl_damage, absorption, godapples, horses, horsehealing, horsearmor, headpost,
            goldenheads, natural_regeneration, chat;

    public GameConfig() {
        this.apple_rate = 0.01;
        this.flint_rate = 0.45;
        this.horses = true;
        this.horsehealing = true;
        this.horsearmor = true;
        this.nether = true;
        this.strength_1 = true;
        this.strength_2 = false;
        this.invisibility_potion = true;
        this.regeneration_potion = true;
        this.bedbombs = false;
        this.speed_1 = true;
        this.speed_2 = true;
        this.natural_regeneration = false;
        this.ender_pearl_damage = false;
        this.poison_1 = true;
        this.poison_2 = false;
        this.absorption = true;
        this.goldenheads = true;
        this.headpost = true;
        this.godapples = false;
        this.map_size = 2000;
        this.max_players = 200;
        this.final_heal_time = 10 * 60;
        this.pvp_time = 20 * 60;
        this.border_time = 45 * 60;
        this.chat = true;
    }

}