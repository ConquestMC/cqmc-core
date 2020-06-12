package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@RequiredArgsConstructor
public class HubCommand implements CommandExecutor {

    private final CorePlugin plugin;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        player.sendMessage(ChatColor.GOLD + "A trusty steed will escort you to hub...");
        sendPlayerToLobby(player);
        return true;
    }

    private void sendPlayerToLobby(Player player) {
        //TODO get available lobby
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("load-balancer");
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
