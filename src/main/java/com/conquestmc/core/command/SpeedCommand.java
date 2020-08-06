package com.conquestmc.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "This is a player only command!");
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission("group.admin")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to do this!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/speed <speed>");
        }

        try {
            int speed = Integer.parseInt(args[0]);

            if (player.isFlying()) {
                player.setFlySpeed(speed);
                player.sendMessage(ChatColor.GREEN + "Fly speed set to: " + speed);
                return true;
            }

            if (player.isOnGround()) {
                player.setWalkSpeed(speed);
                player.sendMessage(ChatColor.GREEN + "Walking speed set to: " + speed);
                return true;
            }

        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Speed must be an integer");
            return true;
        }

        return true;
    }
}
