package com.fygcafe.fygplugin;

import com.fygcafe.fygplugin.commands.UpdateCommand;
import me.youtissoum.Updater;
import org.bukkit.plugin.java.JavaPlugin;

public final class Fygplugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("starting plugin...");
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        Updater updater = new Updater(this, this.getFile(), "youtissoum", "newfygplugin", "fygplugin", getConfig().getString("updater_token"));

        getCommand("updatefygplugin").setExecutor(new UpdateCommand(updater));
        getLogger().info("Plugin started !");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
