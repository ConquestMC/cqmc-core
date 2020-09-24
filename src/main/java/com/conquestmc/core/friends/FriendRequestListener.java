package com.conquestmc.core.friends;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.server.ServerManager;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.core.util.ChatUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class FriendRequestListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equalsIgnoreCase("friend.request")) {

            JsonObject friendReq = new JsonParser().parse(message).getAsJsonObject();
            String from = friendReq.get("from").getAsString();
            String to = friendReq.get("to").getAsString();
            String status = friendReq.get("status").getAsString();
            System.out.println(status);

            FriendRequest request = new FriendRequest(friendReq);

            if (status.equalsIgnoreCase("sent")) {
                if (Bukkit.getPlayer(to) == null || !Bukkit.getPlayer(to).isOnline()) {
                    return;
                }

                Player target = Bukkit.getPlayer(to);

                CorePlugin.getInstance().getPlayer(target).getFriendRequests().add(request);

                String accept = ChatUtil.color("&a&lACCEPT");
                String middle = ChatUtil.color(" &7or");
                String beggining = ChatUtil.color("&e" + from + " &7has requested to be friends! ");
                String decline = ChatUtil.color(" &4&lDECLINE");

                TextComponent acceptComp = new TextComponent(accept);
                acceptComp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/f accept " + from)));
                acceptComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatUtil.color("&aClick to accept the friend request"))}));

                TextComponent declineComp = new TextComponent(decline);
                declineComp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/f decline " + from)));
                declineComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatUtil.color("&cClick to decline the friend request"))}));

                TextComponent msg = new TextComponent(beggining);
                msg.addExtra(acceptComp);
                msg.addExtra(middle);
                msg.addExtra(declineComp);
                target.spigot().sendMessage(msg);
                target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);
                request.setToUUID(target.getUniqueId());
            } else if (status.equalsIgnoreCase("accepted")) {
                System.out.println("Accepted request");
                UUID toUUID = UUID.fromString(friendReq.get("toUUID").getAsString());
                UUID fromUUID = UUID.fromString(friendReq.get("fromUUID").getAsString());
                System.out.println(friendReq.get("fromUUID").getAsString());
                if (CorePlugin.getInstance().isPlayerOnNetwork(from)) {
                    if (Bukkit.getPlayer(fromUUID) != null) {

                        if (Bukkit.getPlayer(fromUUID).isOnline()) {
                            ConquestPlayer player = CorePlugin.getInstance().getPlayer(fromUUID);
                            player.getFriends().add(toUUID);
                            Bukkit.getPlayer(fromUUID).sendMessage(ServerMessages.FRIENDS_PREFIX.getPrefix() + ChatUtil.color("&e" + to + " &ahas accepted your friend request!"));
                            player.removeFriendRequest(to);
                        }
                    }
                }
            } else {
                UUID fromUUID = UUID.fromString(friendReq.get("fromUUID").getAsString());
                if (CorePlugin.getInstance().isPlayerOnNetwork(from)) {
                    if (Bukkit.getPlayer(fromUUID) != null && Bukkit.getPlayer(fromUUID).isOnline()) {

                        ConquestPlayer player = CorePlugin.getInstance().getPlayer(fromUUID);
                        player.getBukkitPlayer().sendMessage(ServerMessages.FRIENDS_PREFIX.getPrefix() + ChatUtil.color("&e" + to + " &chas declined your friend request!"));
                        player.removeFriendRequest(to);
                    }
                }
            }
        }
    }
}
