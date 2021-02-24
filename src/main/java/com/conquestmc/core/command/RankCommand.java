package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.foundation.API;
import com.conquestmc.foundation.CorePlayer;
import com.conquestmc.foundation.player.FProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RankCommand implements CommandExecutor {

    private CorePlugin plugin;

    public RankCommand(CorePlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("group.owner")) {
            commandSender.sendMessage(ServerMessages.PLAYER_NO_PERMISSION.getPrefix());
            return true;
        }

        String targetName = strings[0];
        String rankName = strings[1];

        if (Bukkit.getPlayer(targetName) ==null) {
            commandSender.sendMessage(ChatColor.RED + "This player is not online at this time!");
            return true;
        }

        if (plugin.getServerConfig().getRankByName(rankName) == null) {
            commandSender.sendMessage(ChatColor.RED + "This rank does not exist!");
            return true;
        }

        CorePlayer corePlayer = (CorePlayer) API.getUserManager().findByName(targetName);
        FProfile mainProfile = corePlayer.getProfile("main");
        if (mainProfile.getString("rank").equalsIgnoreCase(rankName)) {
            commandSender.sendMessage(ChatColor.RED + "This user already has this rank!");
            return true;
        }

        mainProfile.set("rank", rankName);
        plugin.applyPermissions(Bukkit.getPlayer(targetName), plugin.getServerConfig().getRankByName(rankName));
        corePlayer.update();

        return false;
    }
}
