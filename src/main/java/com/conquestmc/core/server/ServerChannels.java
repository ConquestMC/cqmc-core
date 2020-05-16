package com.conquestmc.core.server;

public enum ServerChannels {

    STATUS_UPDATE("server.status.update"),
    CREATE_SERVER("server.creation"),
    CREATION_RESPONSE("server.creation.response"),
    SERVER_PING_REQUEST("server.ping.request"),
    SERVER_PING_RESPONSE("server.ping.response"),
    SERVER_FETCH_REQUEST("server.fetch.request"),
    SERVER_FETCH_RESPONSE("server.fetch.response"),
    FIND_PLAYER("find.player");

    private String channel;
    ServerChannels(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
