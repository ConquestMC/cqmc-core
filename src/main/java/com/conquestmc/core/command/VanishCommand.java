package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    private CorePlugin plugin;

    public VanishCommand(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player sender = (Player) commandSender;

        if (!sender.hasPermission("group.admin")) {
            return true;
        }

        plugin.getPlayerManager().getOrInitPromise(sender.getUniqueId()).whenComplete(((conquestPlayer, throwable) -> {
            conquestPlayer.getPlayerSettings().put("vanished", true);
        }));

        sender.sendMessage(ChatColor.GREEN + "you have been vanished!");

        return false;
    }
}
