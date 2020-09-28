package com.conquestmc.core.command;

import com.conquestmc.core.server.ServerManager;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.core.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ServerMessages.SENDER_INVALID.getPrefix());
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission("group.admin")) {
            player.sendMessage(ServerMessages.PLAYER_NO_PERMISSION.getPrefix());
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&c/speed <speed>"));
        }

        try {
            int speed = Integer.parseInt(args[0]);

            if (player.isFlying()) {
                player.setFlySpeed(speed);
                player.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&aFly speed set to: &e" + speed));
                return true;
            }

            if (player.isOnGround()) {
                player.setWalkSpeed(speed);
                player.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&aWalking speed set to: &e" + speed));
                return true;
            }

        } catch (NumberFormatException e) {
            player.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&cSpeed must be an integer"));
            return true;
        }
        return true;
    }
}
