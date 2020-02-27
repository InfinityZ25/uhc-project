package me.infinityz.commands;

import me.infinityz.UHC;

/**
 * CommandManager
 */
public class CommandManager {

    private UHC instance;

    public CommandManager(UHC instance) {
        this.instance = instance;
        GlobalCommands s = new GlobalCommands(instance);
        this.instance.getCommand("uhc").setExecutor(s);
        this.instance.getCommand("heal").setExecutor(s);
        this.instance.getCommand("feed").setExecutor(s);
        this.instance.getCommand("fix").setExecutor(s);
        this.instance.getCommand("respawn").setExecutor(s);
        this.instance.getCommand("kt").setExecutor(s);
        this.instance.getCommand("dq").setExecutor(s);
        this.instance.getCommand("findoff").setExecutor(s);
        this.instance.getCommand("helpop").setExecutor(s);
        this.instance.getCommand("latescatter").setExecutor(s);
        this.instance.getCommand("invsee").setExecutor(s);
        this.instance.getCommand("msg").setExecutor(s);
    }
}