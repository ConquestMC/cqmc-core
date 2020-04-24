package com.conquestmc.core.command;

import org.apache.commons.lang.WordUtils;
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

            if (gameMode.equalsIgnoreCase("c")) {
                gameMode = "CREATIVE";
            }
            if (gameMode.equalsIgnoreCase("a")) {
                gameMode = "ADVENTURE";
            }
            if (gameMode.equalsIgnoreCase("s")) {
                gameMode = "SURVIVAL";
            }

            GameMode mode = GameMode.valueOf(gameMode);
            player.setGameMode(mode);
            player.sendMessage(ChatColor.GREEN + "Your game mode has been updated to: " + ChatColor.GOLD + WordUtils.capitalizeFully(mode.name()));
        }
        else if (args.length == 2) {
            //TODO set other players mode
        }
        return false;
    }
}
