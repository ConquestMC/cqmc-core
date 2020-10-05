package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.command.framework.CCommand;
import com.conquestmc.core.command.framework.CommandInfo;
import com.conquestmc.core.command.framework.CommandPermission;
import com.conquestmc.foundation.API;
import com.conquestmc.foundation.CorePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "perm", description = "command to give, remove and check permissions of a player")
@CommandPermission(permission = "rank.manager")
public class PermissionCommand implements CCommand {

    /*
        /perm give user perm
        /perm remove user perm
        /perm check user perm
     */

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 3) {
            String user = args[1];
            String permission = args[2];
            CorePlayer player = (CorePlayer) API.getUserManager().findByName(user);

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Cannot find user " + user);
                return false;
            }

            if (args[0].equalsIgnoreCase("give")) {

                if (player.hasPermission(permission)) {
                    sender.sendMessage(ChatColor.RED + "This user already has the permission " + permission);
                    return false;
                }

                player.givePermission(permission);
            }
        }
        return false;
    }
}
