package com.conquestmc.core.command;

import com.conquestmc.core.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PluginCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("pl") || command.getName().equalsIgnoreCase("plugins")) {
            if (!commandSender.hasPermission("core.all")) {
                commandSender.sendMessage(ChatUtil.color("&cThey're all custom"));
                return true;
            }
        }
        return false;
    }
}
