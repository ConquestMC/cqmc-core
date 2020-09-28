package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.server.ServerManager;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.core.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Ethan Borawski
 */
public class CosmeticCommand implements CommandExecutor {

    private CorePlugin plugin;

    public CosmeticCommand(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        String targetName = args[0];
        String cosmeticName = args[1];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(ChatUtil.color("&cCannot find specified player!"));
            return true;
        }

        ConquestPlayer targetPlayer = plugin.getPlayer(target.getUniqueId());
        if (targetPlayer.getCosmetics().contains(cosmeticName)) {
            sender.sendMessage(ServerMessages.FRIENDS_PREFIX.getPrefix() + ChatUtil.color("&cThe player already has this cosmetic!"));
            return true;
        }

        targetPlayer.unlockCosmetic(cosmeticName);
        target.sendMessage(ServerMessages.FRIENDS_PREFIX.getPrefix() + ChatUtil.color("&7You now have &d" + cosmeticName));
        sender.sendMessage(ServerMessages.FRIENDS_PREFIX.getPrefix() + ChatUtil.color("&7Awarded &d" + cosmeticName + " &7to &d" + targetName + ""));

        return false;
    }
}
