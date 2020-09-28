package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.server.ServerManager;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.core.util.ChatUtil;
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
            commandSender.sendMessage(ServerMessages.SENDER_INVALID.getPrefix());
            return true;
        }

        Player sender = (Player) commandSender;

        if (!sender.hasPermission("group.admin")) {
            sender.sendMessage(ServerMessages.PLAYER_NO_PERMISSION.getPrefix());
            return true;
        }

        plugin.getPlayerManager().getOrInitPromise(sender.getUniqueId()).whenComplete(((conquestPlayer, throwable) -> {
            conquestPlayer.getPlayerSettings().put("vanished", true);
        }));

        sender.sendMessage(ServerMessages.VANISH_PREFIX.getPrefix() + ChatUtil.color("&f&l*POOF* &ayou have been vanished!"));
        return false;
    }
}
