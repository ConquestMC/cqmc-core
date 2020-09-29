package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.server.ServerManager;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.core.util.ChatUtil;
import com.conquestmc.foundation.API;
import com.conquestmc.foundation.CorePlayer;
import com.conquestmc.foundation.player.FProfile;
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

        CorePlayer player = (CorePlayer) API.getUserManager().findByUniqueId(sender.getUniqueId());
        FProfile settings = player.getProfile("settings");
        settings.set("vanished", true);

        sender.sendMessage(ServerMessages.VANISH_PREFIX.getPrefix() + ChatUtil.color("&f&l*POOF* &ayou have been vanished!"));
        return false;
    }
}
