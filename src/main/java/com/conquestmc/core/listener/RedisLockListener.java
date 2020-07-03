package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

@RequiredArgsConstructor
public class RedisLockListener extends JedisPubSub {


    private final CorePlugin plugin;

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equalsIgnoreCase("redis.lock")) {
            JsonObject object = new JsonParser().parse(message).getAsJsonObject();
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());

            if (plugin.getPlayerManager().getPlayers().get(uuid) == null) {
                return;
            }
            plugin.getPlayerManager().updatePlayer(uuid).whenComplete((doc, throwable) -> {
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try (Jedis j = plugin.getJedisPool().getResource()) {
                        j.del("status." + uuid);
                    }
                });
            });
        }
    }
}
