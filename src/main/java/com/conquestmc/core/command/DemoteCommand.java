package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.player.Rank;
import com.conquestmc.core.player.StaffRank;
import com.conquestmc.core.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DemoteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("core.demote")) {
            commandSender.sendMessage(ChatUtil.color("&cYou do not have permission to do this!"));
            return true;
        }

        if (args.length != 1) {
            commandSender.sendMessage(ChatUtil.color("&c/demote <user>"));
            return true;
        }
        String userName = args[0];
        Player target = Bukkit.getPlayer(userName);

        if (target == null) {
            commandSender.sendMessage(ChatUtil.color("&cCannot find the player specified!"));
            return true;
        }

        List<Rank> ranks = CorePlugin.getInstance().getPlayer(target).getRanks();
        Rank staffRank = null;
        for (Rank rank : ranks) {
            if (rank instanceof StaffRank) {
                staffRank = rank;
            }
        }
        if (staffRank == null) {
            commandSender.sendMessage(ChatUtil.color("&cCannot demote this user as they have no staff rank!"));
            return true;
        }

        ranks.remove(staffRank);
        CorePlugin.getInstance().getPlayer(target).updatePrefixedRank();
        commandSender.sendMessage(ChatUtil.color("&aYou have demoted &e" + target.getName()));
        return true;
    }
}
