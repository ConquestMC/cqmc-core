package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.player.Rank;
import com.conquestmc.core.player.StaffRank;
import com.conquestmc.core.server.ServerManager;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.core.util.ChatUtil;
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
            sender.sendMessage(ServerMessages.PLAYER_NO_PERMISSION.getPrefix());
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&c/giverank <user> <rank>"));
            return true;
        }

        String targetName = args[0];
        String rankName = args[1];

        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&cCannot find specified player!"));
            return true;
        }

        Rank rank = plugin.getRankManager().getRank(rankName.toLowerCase());

        if (rank == null) {
            sender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&cCannot find the rank specified!"));
            return true;
        }

        if (plugin.getPlayerManager().getConquestPlayer(target.getUniqueId()).hasRank(rank)) {
            sender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&cThe player already has this rank!"));
            return true;
        }

        plugin.getPlayerManager().getConquestPlayer(target.getUniqueId()).getRanks().add(rank);
        sender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&aYou have given &e" + target.getName() + " &aa new rank!"));
        target.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&aYou have been awarded the rank: ") + ChatColor.translateAlternateColorCodes('&', rank instanceof StaffRank ? rank.getPrefix() : rank.getPrefix() + rank.getName()));

        plugin.getPlayer(target).updatePrefixedRank();
        return true;
    }
}
