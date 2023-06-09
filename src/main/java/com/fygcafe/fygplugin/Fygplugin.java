package com.fygcafe.fygplugin;

import com.fygcafe.fygplugin.commands.TickedRestartCommand;
import com.fygcafe.fygplugin.commands.UpdateCommand;
import me.youtissoum.Updater;
import org.bukkit.plugin.java.JavaPlugin;

public final class Fygplugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("starting plugin...");
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        Updater updater = new Updater(this, this.getFile(), "youtissoum", "newfygplugin", "fygplugin");

        getCommand("updatefygplugin").setExecutor(new UpdateCommand(this, updater));
        getCommand("tickedrestart").setExecutor(new TickedRestartCommand(this));
        getLogger().info("Plugin started !");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
