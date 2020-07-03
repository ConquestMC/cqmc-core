package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
            sender.sendMessage(ChatColor.RED + "Cannot find specified player!");
            return true;
        }

        ConquestPlayer targetPlayer = plugin.getPlayer(target.getUniqueId());
        if (targetPlayer.getCosmetics().contains(cosmeticName)) {
            sender.sendMessage(ChatColor.RED + "The player already has this cosmetic!");
            return true;
        }

        targetPlayer.unlockCosmetic(cosmeticName);
        target.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.LIGHT_PURPLE + cosmeticName);
        sender.sendMessage(ChatColor.GRAY + "Awarded " + ChatColor.LIGHT_PURPLE + cosmeticName + ChatColor.GRAY + " to " + targetName + "");

        return false;
    }
}
