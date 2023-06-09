package com.fygcafe.fygplugin.commands;

import com.fygcafe.fygplugin.Fygplugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TickedRestartCommand implements CommandExecutor {
    private final Fygplugin plugin;
    private int timer;

    public TickedRestartCommand(Fygplugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.timer = 10;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                if(timer == 0) {
                    Bukkit.spigot().restart();
                }
                Bukkit.broadcast(Component.text("§cRestarting in : " + timer));
                timer--;
            }
        }, 0L, 20L);
        return true;
    }
}
