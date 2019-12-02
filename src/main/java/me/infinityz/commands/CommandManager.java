package me.infinityz.commands;

import me.infinityz.UHC;

/**
 * CommandManager
 */
public class CommandManager {

    private UHC instance;

    public CommandManager(UHC instance){
        this.instance = instance;
        this.instance.getCommand("uhc").setExecutor(new GlobalCommands(instance));
    }
}