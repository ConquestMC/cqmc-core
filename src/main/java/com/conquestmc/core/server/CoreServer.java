package com.conquestmc.core.server;

import lombok.Data;

@Data
public class CoreServer {

    private ServerType type;
    private String name;
    private String ip, port;

    public CoreServer(ServerType type, String name, String address) {
        this.type = type;
        this.name = name;
        this.ip = address.split(":")[0];
        this.port = address.split(":")[1];
    }

    public String getAddress() {
        return ip + ":" + port;
    }
}
