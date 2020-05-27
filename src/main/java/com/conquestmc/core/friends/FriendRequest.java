package com.conquestmc.core.friends;

import com.conquestmc.core.CorePlugin;
import com.google.gson.JsonObject;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
            Bukkit.getPlayer(from).sendMessage(ChatColor.AQUA + "Friends >> " + ChatColor.RED + to + " is not online at this time!");
            return;
        }

        if (Bukkit.getPlayer(to) != null && Bukkit.getPlayer(to).isOnline()) {
            //Manually send
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
        Bukkit.getPlayer(from).sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "Friends >> " + ChatColor.GREEN + "Sent a friend request to " + ChatColor.YELLOW + to);
    }

    public void changeStatus(String status) {
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
