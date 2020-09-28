package com.conquestmc.core.command;

import com.conquestmc.core.server.ServerManager;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.core.util.ChatUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender.hasPermission("core.gamemode"))) {
            sender.sendMessage(ServerMessages.PLAYER_NO_PERMISSION.getPrefix());
            return false;
        }

        if (args.length == 1) {
            String gameMode = args[0];

            if (!(sender instanceof Player)) {
                sender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&cPlease provide a target!"));
                return true;
            }

            Player player = (Player) sender;

            GameMode mode = getMode(gameMode);

            if (mode == null) {
                player.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&cNot a valid gamemode!"));
                return true;
            }

            player.setGameMode(mode);
            player.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&aYour game mode has been updated to: &6" + WordUtils.capitalizeFully(mode.name())));
        } else if (args.length == 2) {
            if (!(sender.hasPermission("core.gamemode.others"))) {
                sender.sendMessage(ServerMessages.PLAYER_NO_PERMISSION.getPrefix());
                return true;
            }

            String targetName = args[1];
            Player target = Bukkit.getPlayer(targetName);

            if (target == null || !target.isOnline()) {
                sender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&cCannot find the specified player!"));
                return true;
            }

            GameMode mode = getMode(args[0]);

            if (mode == null) {
                sender.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&cNot a valid gamemode!"));
                return true;
            }

            target.setGameMode(mode);
            target.sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color("&aYour game mode has been updated to: &6" + WordUtils.capitalizeFully(mode.name())));
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
