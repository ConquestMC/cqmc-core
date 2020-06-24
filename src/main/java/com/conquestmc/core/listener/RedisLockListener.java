package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.player.PlayerManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import sun.lwawt.macosx.CThreading;

import java.util.UUID;

@RequiredArgsConstructor
public class RedisLockListener extends JedisPubSub {


    private final CorePlugin plugin;


    @Override
    public void onMessage(String channel, String message) {
        if (channel.equalsIgnoreCase("redis.lock")) {
            JsonObject object = new JsonParser().parse(message).getAsJsonObject();
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            String port = object.get("server").getAsString();

            plugin.getPlayerManager().updatePlayer(uuid).whenComplete((doc, throwable) -> {
                try (Jedis j = plugin.getJedisPool().getResource()) {
                    j.set("status." + uuid.toString(), port);
                }
            });
        }
    }
}
