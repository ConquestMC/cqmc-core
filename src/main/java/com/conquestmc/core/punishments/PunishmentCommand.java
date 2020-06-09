package com.conquestmc.core.punishments;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PunishmentCommand implements CommandExecutor {

    private final PunishmentManager punishmentManager;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender.hasPermission("staff.punish")) {
            if (args.length == 3) {

                String playerName = args[0];
                Player punished = Bukkit.getPlayer(playerName);
                if (punished == null) {
                    sender.sendMessage(ChatColor.RED + "Cannot find player you want to punish!");
                    return true;
                }

                try {
                    PunishmentType type = PunishmentType.valueOf(args[1].toUpperCase());
                    int severity = Integer.parseInt(args[2]);

                    punishmentManager.punishPlayer(punished.getUniqueId(), type, severity);
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        if (pl.hasPermission("core.staff")) {
                            pl.sendMessage(ChatColor.AQUA + "Staff >> " + ChatColor.YELLOW + sender.getName() + ChatColor.GOLD + " has punished " + ChatColor.YELLOW + punished.getName());
                        }
                        else {
                            pl.sendMessage(ChatColor.AQUA + "Conquest >> " + ChatColor.DARK_GREEN + "Someone in your game has been found breaking the rules and has received repercussions.");
                        }
                    }
                    return true;
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "/punish <player> <chat|hacking|gameplay> <severity>");
                    return true;
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "/punish <player> <chat|hacking|gameplay> <severity>");
                return true;
            }
        }
        return false;
    }
}
