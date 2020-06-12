package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.player.Rank;
import com.conquestmc.core.player.StaffRank;
import org.apache.commons.lang.WordUtils;
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
            sender.sendMessage(ChatColor.RED + "/giverank <user> <rank>");
            return true;
        }

        String targetName = args[0];
        String rankName = args[1];

        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Cannot find specified player!");
            return true;
        }

        Rank rank = plugin.getRankManager().getRank(rankName.toLowerCase());

        if (rank == null) {
            sender.sendMessage(ChatColor.RED + "Cannot find the rank specified!");
            return true;
        }

        if (plugin.getPlayerManager().getConquestPlayer(target.getUniqueId()).hasRank(rank)) {
            sender.sendMessage(ChatColor.RED + "The player already has this rank!");
            return true;
        }

        plugin.getPlayerManager().getConquestPlayer(target.getUniqueId()).getRanks().add(rank);
        sender.sendMessage(ChatColor.GREEN + "You have given " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + " a new rank!");
        target.sendMessage(ChatColor.GREEN + "You have been awarded the rank: " + ChatColor.translateAlternateColorCodes('&', rank.getPrefix() + rank.getName()));
        return true;
    }
}
