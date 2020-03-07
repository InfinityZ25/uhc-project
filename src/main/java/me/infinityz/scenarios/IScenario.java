package me.infinityz.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import me.infinityz.UHC;

/**
 * ScenarioSkeleton
 */
public class IScenario implements Listener {
    /*
     * Create an object that can be used as a base for every single other scenarios.
     * Figure this out later. No need to worry just yet.
     */

    public boolean enabled = false;
    public String description = "This is the default scenario description. \nEach scenario should have a different one.";

    public IScenario() {
        registerScenario();
    }

    public void registerScenario() {
        System.out.println(this.getClass().getSimpleName() + " has been registered!");
    }

    public void enableScenario() {
        Bukkit.getPluginManager().registerEvents(this, UHC.getInstance());
        this.enabled = true;
    }

    public void disableScenario() {
        HandlerList.unregisterAll(this);
        this.enabled = false;
    }

    // Add editor item and display name, display lore, and overchat lore.

}