package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.core.util.ChatUtil;
import com.conquestmc.foundation.CorePlayer;
import com.conquestmc.foundation.player.FProfile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

        CorePlayer targetPlayer = plugin.getPlayer(target.getUniqueId());
        FProfile mainProfile = targetPlayer.getProfile("main");

        List<String> unlockedCosmetics = (List<String>) mainProfile.getObject("unlockedCosmetics");


        if (unlockedCosmetics.contains(cosmeticName)) {
            sender.sendMessage(ServerMessages.FRIENDS_PREFIX.getPrefix() + ChatUtil.color("&cThe player already has this cosmetic!"));
            return true;
        }

        unlockedCosmetics.add(cosmeticName);
        mainProfile.set("unlockedCosmetics", unlockedCosmetics);
        targetPlayer.update();

        target.sendMessage(ServerMessages.FRIENDS_PREFIX.getPrefix() + ChatUtil.color("&7You now have &d" + cosmeticName));
        sender.sendMessage(ServerMessages.FRIENDS_PREFIX.getPrefix() + ChatUtil.color("&7Awarded &d" + cosmeticName + " &7to &d" + targetName + ""));

        return false;
    }
}
