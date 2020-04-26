package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {

    private CorePlugin plugin;

    public RankCommand(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("core.setrank")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this!");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "/setrank <user> <rank>");
            return true;
        }

        String targetName = args[0];
        String rank = args[1];

        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Cannot find specified player!");
            return true;
        }

        plugin.getPlayer(target.getUniqueId()).setRank(Rank.valueOf(rank));
        target.sendMessage(ChatColor.GREEN + "Your rank has been set to: " + ChatColor.translateAlternateColorCodes('&', Rank.valueOf(rank).getPrefix()));
        return true;
    }
}
