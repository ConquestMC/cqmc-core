package com.conquestmc.core.punishments;

import com.conquestmc.core.server.ServerManager;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.core.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
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
                    sender.sendMessage(ServerMessages.PUNISH_PREFIX.getPrefix() + ChatUtil.color("&cCannot find player you want to punish!"));
                    return true;
                }

                try {
                    PunishmentType type = PunishmentType.valueOf(args[1].toUpperCase());
                    int severity = Integer.parseInt(args[2]);

                    punishmentManager.punishPlayer(punished.getUniqueId(), type, severity);
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        if (pl.hasPermission("core.staff")) {
                            pl.sendMessage(ServerMessages.PUNISH_PREFIX.getPrefix() + ChatUtil.color("&e" + sender.getName() + " &6has punished &e" + punished.getName()));
                        } else {
                            pl.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&2Someone in your game has been found breaking the rules and has received repercussions."));
                        }
                    }
                    return true;
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ServerMessages.PUNISH_PREFIX.getPrefix() + ChatUtil.color("&c/punish <player> <chat|hacking|gameplay> <severity>"));
                    return true;
                }
            } else {
                sender.sendMessage(ServerMessages.PUNISH_PREFIX.getPrefix() + ChatUtil.color("&c/punish <player> <chat|hacking|gameplay> <severity>"));
                return true;
            }
        }
        return false;
    }
}
