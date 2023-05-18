package com.eneskey.paper.myplug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;



public final class MyPlug extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("plug").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

                if (strings.length == 0) {
                    commandSender.sendMessage("Reload plugin: /plug reload");
                    return true;
                }

                if (strings[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    commandSender.sendMessage("Plugin reloaded");
                    return true;
                }

                return true;
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
