package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
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
public class DrachmaCommand implements CommandExecutor {

    private final CorePlugin plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("group.admin")) {
            commandSender.sendMessage(ServerMessages.PLAYER_NO_PERMISSION.getPrefix());
            return true;
        }

        //set|give|take user amount

        if (strings.length == 3) {
            String action = strings[0];
            String userName = strings[1];
            int amount = 0;

            try {
                amount = Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (Bukkit.getPlayer(userName) == null) {
                commandSender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&cPlayer is not on this server!")); //todo shouldn't be handled like this, should just update the value stored in db if not online
                return true;
            }

            Player pl = Bukkit.getPlayer(userName);
            ConquestPlayer cp = plugin.getPlayer(pl.getUniqueId());

            if (action.equalsIgnoreCase("set")) {
                cp.setCoins(amount);
            } else if (action.equalsIgnoreCase("give")) {
                cp.setCoins(cp.getCoins() + amount);
            } else if (action.equalsIgnoreCase("take")) {
                cp.setCoins(cp.getCoins() - amount);
            } else {
                return true;
            }
            commandSender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&aChanged &b" + userName + "&a's balance to: &e" + cp.getCoins()));
        } else {
            commandSender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&c/drachma set|give|take user amount"));
            return true;
        }

        return false;
    }
}
