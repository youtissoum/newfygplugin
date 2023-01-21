package com.fygcafe.fygplugin.commands;

import me.youtissoum.Updater;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class UpdateCommand implements CommandExecutor {
    private Updater updater;
    public UpdateCommand(Updater updater) {
        this.updater = updater;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        updater.update();

        return true;
    }
}
