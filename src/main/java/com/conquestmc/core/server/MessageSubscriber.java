package com.conquestmc.core.server;

import redis.clients.jedis.JedisPubSub;

public class MessageSubscriber extends JedisPubSub {

    /**
     * {
     *     "success": true,
     *     "name": "name",
     *     "type": "GAME"
     * }
     *
     */

    @Override
    public void onMessage(String channel, String message) {
        super.onMessage(channel, message);

        if (channel.equalsIgnoreCase(ServerChannels.CREATION_RESPONSE.getChannel())) {
            //Server response message;
        }
    }
}
