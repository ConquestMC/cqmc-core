package com.conquestmc.core.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.JedisPubSub;

public class ServerListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equalsIgnoreCase("servers.request.response")) {
            JsonObject object = new JsonParser().parse(message).getAsJsonObject();
        }
    }
}
