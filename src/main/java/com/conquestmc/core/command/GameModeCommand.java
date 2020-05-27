package com.conquestmc.core.command;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender.hasPermission("core.gamemode"))) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this!");
            return false;
        }

        if (args.length == 1) {
            String gameMode = args[0];

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Please provide a target!");
                return true;
            }

            Player player = (Player) sender;

            GameMode mode = getMode(gameMode);

            if (mode == null) {
                player.sendMessage(ChatColor.RED + "Not a valid gamemode!");
                return true;
            }

            player.setGameMode(mode);
            player.sendMessage(ChatColor.GREEN + "Your game mode has been updated to: " + ChatColor.GOLD + WordUtils.capitalizeFully(mode.name()));
        }
        else if (args.length == 2) {
            if (!(sender.hasPermission("core.gamemode.others"))) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to do this!");
                return true;
            }

            String targetName = args[1];
            Player target = Bukkit.getPlayer(targetName);

            if (target == null || !target.isOnline()) {
                sender.sendMessage(ChatColor.RED + "Cannot find the specified player!");
                return true;
            }

            GameMode mode = getMode(args[0]);

            if (mode == null) {
                sender.sendMessage(ChatColor.RED + "Not a valid gamemode!");
                return true;
            }

            target.setGameMode(mode);
            target.sendMessage(ChatColor.GREEN + "Your game mode has been updated to: " + ChatColor.GOLD + WordUtils.capitalizeFully(mode.name()));
        }
        return true;
    }

    private GameMode getMode(String string) {
        GameMode gameMode;
        if (string.equalsIgnoreCase("c")) {
            return GameMode.CREATIVE;
        }
        if (string.equalsIgnoreCase("a")) {
            return GameMode.ADVENTURE;
        }
        if (string.equalsIgnoreCase("s")) {
            return GameMode.SURVIVAL;
        }
        return null;
    }
}
