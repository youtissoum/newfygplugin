package com.fygcafe.fygplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Fygplugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("starting plugin...");
        getLogger().info("Plugin started !");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
