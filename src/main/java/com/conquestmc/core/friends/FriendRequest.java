package com.conquestmc.core.friends;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.util.ChatUtil;
import com.conquestmc.core.util.OldSounds;
import com.google.gson.JsonObject;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

@Data
public class FriendRequest {

    private String from, to;
    private UUID fromUUID, toUUID;
    private long creation;
    private String status;

    public FriendRequest(String from, String to) {
        this.from = from;
        this.to = to;
        this.creation = System.currentTimeMillis();
        this.fromUUID = Bukkit.getPlayer(from).getUniqueId();
    }

    public FriendRequest(JsonObject object) {
        this.from = object.get("from").getAsString();
        this.to = object.get("to").getAsString();
        this.fromUUID = UUID.fromString(object.get("fromUUID").getAsString());

        if (object.get("toUUID") != null) {
            this.toUUID = UUID.fromString(object.get("toUUID").getAsString());
        }
        this.status = object.get("status").getAsString();
    }

    public void send() {
        if (!CorePlugin.getInstance().getOnlinePlayerNames().contains(to)) {
            Bukkit.getPlayer(from).sendMessage(ChatUtil.color("&b&lFriends >> &c" + to + " &eis not online at this time!"));
            return;
        }

        if (Bukkit.getPlayer(to) != null && Bukkit.getPlayer(to).isOnline()) {
            //Manually send
            Player target = Bukkit.getPlayer(to);

            CorePlugin.getInstance().getPlayer(target).getFriendRequests().add(this);

            String accept =  ChatUtil.color("&a&lACCEPT");
            String middle = ChatUtil.color(" &7or");
            String beggining = ChatUtil.color("&e" + from + " &7has requested to be friends! ");
            String decline = ChatUtil.color(" &c&lDECLINE");

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
            target.playSound(target.getLocation(), OldSounds.LEVEL_UP.playSound(), 1, 1);
            this.setToUUID(target.getUniqueId());
        }
        else {
            try (Jedis j = CorePlugin.getInstance().getJedisPool().getResource()) {
                JsonObject request = new JsonObject();
                request.addProperty("from", from);
                request.addProperty("to", to);
                request.addProperty("fromUUID", fromUUID.toString());
                request.addProperty("status", "sent");
                j.publish("friend.request", request.toString());
            }
        }
        Bukkit.getPlayer(from).sendMessage(ChatUtil.color("&b&lFriends >> &aSent a friend request to &e"+ to));
    }

    public void changeStatus(String status) {

        if (status.equalsIgnoreCase( "accepted") && Bukkit.getPlayer(this.getFromUUID()) != null && Bukkit.getPlayer(this.getFromUUID()).isOnline()) {
            ConquestPlayer player = CorePlugin.getInstance().getPlayer(fromUUID);
            player.getFriends().add(toUUID);
            Bukkit.getPlayer(fromUUID).sendMessage(ChatUtil.color("&b&lFriends >> &e" + to + " &ahas accepted your friend request!"));
            player.removeFriendRequest(to);
            return;
        }

        if (status.equalsIgnoreCase( "declined") && Bukkit.getPlayer(this.getFromUUID()) != null && Bukkit.getPlayer(this.getFromUUID()).isOnline()) {
            ConquestPlayer player = CorePlugin.getInstance().getPlayer(fromUUID);
            player.getBukkitPlayer().sendMessage(ChatUtil.color("&b&lFriends >> &e" +  to + " &chas declined your friend request!"));
            player.removeFriendRequest(to);
            return;
        }

        try (Jedis j = CorePlugin.getInstance().getJedisPool().getResource()) {
            JsonObject request = new JsonObject();
            request.addProperty("from", from);
            request.addProperty("to", to);
            request.addProperty("toUUID", Bukkit.getPlayer(to).getUniqueId().toString());
            request.addProperty("status", status);
            request.addProperty("fromUUID", fromUUID.toString());

            j.publish("friend.request", request.toString());
        }
    }
}
