package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class FriendCommand implements CommandExecutor {

    private final CorePlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player pl = (Player) sender;

        if (args.length == 0) {
            pl.sendMessage(ChatColor.YELLOW + " - " + ChatColor.GRAY + "/friends");
            pl.sendMessage(ChatColor.YELLOW + " - " + ChatColor.GRAY + "/friends add [name]");
            pl.sendMessage(ChatColor.YELLOW + " - " + ChatColor.GRAY + "/friends del|delete [name]");
            pl.sendMessage(ChatColor.YELLOW + " - " + ChatColor.GRAY + "/friends list");
            pl.sendMessage(ChatColor.YELLOW + " - " + ChatColor.GRAY + "/friends requests");
            pl.sendMessage(ChatColor.YELLOW + " - " + ChatColor.GRAY + "/friends on/off");
            return true;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2) {
                pl.sendMessage(ChatColor.RED + "/f add [player]");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                pl.sendMessage(ChatColor.RED + "Cannot find the specified player!");
                return true;
            }

            ConquestPlayer send = plugin.getPlayer(pl.getUniqueId());
            send.sendFriendRequest(target);
        }
        else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                pl.sendMessage(ChatColor.RED + "/f delete [player]");
            }
            ConquestPlayer send = plugin.getPlayer(pl);
            Player target = Bukkit.getPlayer(args[1]);
            if (!send.getFriends().contains(target.getUniqueId())) {
                pl.sendMessage(ChatColor.RED + "This person could not be found on your friends list!");
                return true;
            }
            send.getFriends().remove(target.getUniqueId());
            pl.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "Friends >> " + ChatColor.GREEN + " removed " + ChatColor.GRAY + args[1] + ChatColor.GREEN + " from your friends list!");
        }
        else if (args[0].equalsIgnoreCase("list")) {
            ConquestPlayer player = plugin.getPlayer(pl);

            pl.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "Friends >>");
            for (UUID uuid : player.getFriends()) {
                pl.sendMessage(ChatColor.YELLOW + "    - " + ChatColor.YELLOW + Bukkit.getPlayer(uuid).getName());
            }
        }
        return true;
    }
}
