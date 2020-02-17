package me.infinityz.commands;

import me.infinityz.UHC;

/**
 * CommandManager
 */
public class CommandManager {

    private UHC instance;

    public CommandManager(UHC instance) {
        this.instance = instance;
        this.instance.getCommand("uhc").setExecutor(new GlobalCommands(instance));
        this.instance.getCommand("heal").setExecutor(new GlobalCommands(instance));
        this.instance.getCommand("feed").setExecutor(new GlobalCommands(instance));
        this.instance.getCommand("fix").setExecutor(new GlobalCommands(instance));
        this.instance.getCommand("respawn").setExecutor(new GlobalCommands(instance));
        this.instance.getCommand("kt").setExecutor(new GlobalCommands(instance));
        this.instance.getCommand("dq").setExecutor(new GlobalCommands(instance));
        this.instance.getCommand("findoff").setExecutor(new GlobalCommands(instance));
    }
}