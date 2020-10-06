package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.foundation.API;
import com.conquestmc.foundation.CorePlayer;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PermissionChannelListener implements PluginMessageListener {


    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.contains("permissions")) {
            System.out.println("Received message on channel: " + message.toString());

            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            in.readUTF();
            in.readUTF();
            String sub = in.readUTF();
            String user = in.readUTF();
            String perm = in.readUTF();

            System.out.println("sub: " + sub);
            System.out.println("user: " + user);
            System.out.println("perm: " + perm);

            if (sub.equalsIgnoreCase("give")) {
                //fuck ya permission
                CorePlayer corePlayer = (CorePlayer) API.getUserManager().findByName(user);
                if (corePlayer == null)
                    return;

                if (corePlayer.isOnline()) {
                    //recalc
                    Player target = Bukkit.getPlayer(user);
                    if (target == null)
                        return;

                    if (target.isOnline()) {
                        CorePlugin.getInstance().addPermission(target, perm);
                        System.out.println("Added permission, and value is: " + player.hasPermission(perm));
                    }
                }
            }
            else if (sub.equalsIgnoreCase("remove")) {
                CorePlayer corePlayer = (CorePlayer) API.getUserManager().findByName(user);
                if (corePlayer == null)
                    return;

                if (corePlayer.isOnline()) {
                    //recalc
                    Player target = Bukkit.getPlayer(user);
                    if (target == null)
                        return;

                    if (target.isOnline()) {
                        CorePlugin.getInstance().removePermission(target, perm);
                        System.out.println("Added permission, and value is: " + player.hasPermission(perm));
                    }
                }
            }
        }
    }
}
