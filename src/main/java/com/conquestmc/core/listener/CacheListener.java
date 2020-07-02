package com.conquestmc.core.listener;

import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.player.PlayerManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

@RequiredArgsConstructor
public class CacheListener extends JedisPubSub {

    private final PlayerManager playerManager;

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equalsIgnoreCase("user.cache")) {



        /*
            Message:
            {
                from: port,
                to: port
                uuid: uuid,
                doc: doc,
            }
         */

            JsonObject object = new JsonParser().parse(message).getAsJsonObject();

            int port = object.get("port").getAsInt();

            if (Bukkit.getServer().getPort() != port) {
                return;
            }

            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            Document doc = Document.parse(object.get("doc").getAsString());

            playerManager.getPlayers().put(uuid, new ConquestPlayer(uuid, doc));
            System.out.println("Got cached player ");
        } else if (channel.equalsIgnoreCase("player.disconnect")) {
            JsonObject object = new JsonParser().parse(message).getAsJsonObject();

            int port = object.get("port").getAsInt();

            if (Bukkit.getServer().getPort() != port) {
                return;
            }

            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            playerManager.pushPlayer(uuid);
        }
    }
}