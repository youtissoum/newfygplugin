package com.fygcafe.fygplugin.commands;

import com.fygcafe.fygplugin.Fygplugin;
import me.youtissoum.Updater;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UpdateCommand implements CommandExecutor {
    private final Fygplugin plugin;
    private final Updater updater;
    public UpdateCommand(Fygplugin plugin, Updater updater) {
        this.plugin = plugin;
        this.updater = updater;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            updater.update();
        } catch (IOException e) {
            this.plugin.getLogger().severe("Unable to download the update.");
            return false;
        }

        return true;
    }
}
