package com.conquestmc.core.server;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.exception.ServerCreationException;
import com.google.common.collect.Lists;
import com.google.gson.*;
import lombok.Data;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class ServerManager {

    private final CorePlugin plugin;
    private CoreServer currentServer = null;
    private List<CoreServer> networkServers = Lists.newArrayList();
    List<String> channels;
    String[] myArray;


    /**
     * TODO when this server boots up send the creation if its config is not setup
     * Finish fetch request
     */

    public ServerManager(CorePlugin plugin) {
        this.plugin = plugin;
        this.channels = Arrays.stream(ServerChannels.values()).map(ServerChannels::getChannel).collect(Collectors.toList());
        this.myArray = new String[channels.size()];
        listen();
        sendCreateRequest(plugin.getServerConfig().getType());

        requestServerUpdate();
    }

    public void sendCreateRequest(ServerType type) {
        try (Jedis jedis = plugin.getJedisPool().getResource()) {
            JsonObject msg = new JsonObject();
            msg.addProperty("type", type.name());
            jedis.publish(ServerChannels.CREATE_SERVER.getChannel(), msg.toString());
        }
    }

    public void requestServerUpdate() {
        try (Jedis jedis = plugin.getJedisPool().getResource()) {
            JsonObject object = new JsonObject();
            object.addProperty("server", this.currentServer.getName());
            jedis.publish(ServerChannels.SERVER_FETCH_REQUEST.getChannel(), object.toString());
        }
    }

    private void addServer(CoreServer server) {
        this.networkServers.add(server);
        plugin.getLogger().info("Registered server: " + server.getName() + " at address: " + server.getAddress());
    }

    public List<CoreServer> getServersOfType(ServerType type) {
        return networkServers.stream().filter(s -> s.getType() == type).collect(Collectors.toList());
    }

    public void listen() {
        try (Jedis jedis = plugin.getJedisPool().getResource()) {
            new Thread(() -> {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {

                        if (channel.equalsIgnoreCase(ServerChannels.CREATION_RESPONSE.getChannel())) {
                            plugin.getServer().getLogger().info("Received server creation response...");
                            JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                            boolean success = object.get("success").getAsBoolean();

                            if (!success) {
                                try {
                                    throw new ServerCreationException("This server instance could not be created!");
                                } catch (ServerCreationException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                String name = object.get("name").getAsString();
                                ServerType type = ServerType.valueOf(object.get("type").getAsString());
                                CoreServer server = new CoreServer(type, name, plugin.getServerConfig().getServerAddress());
                                currentServer = server;
                                addServer(server);
                            }
                        } else if (channel.equalsIgnoreCase(ServerChannels.SERVER_PING_REQUEST.getChannel())) {
                            plugin.getServer().getLogger().info("Received Bungee request for servers...");
                            JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                            JsonArray array = object.get("known").getAsJsonArray();

                            if (!array.toString().contains(currentServer.getName())) {
                                JsonObject server = new JsonObject();
                                server.addProperty("address", currentServer.getAddress());
                                server.addProperty("name", currentServer.getName());
                                jedis.publish(ServerChannels.SERVER_PING_RESPONSE.getChannel(), server.toString());
                            }
                        } else if (channel.equalsIgnoreCase(ServerChannels.SERVER_FETCH_RESPONSE.getChannel())) {
                            //Lets look at all the servers :D
                            JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                            JsonArray servers = object.get("servers").getAsJsonArray();
                            getNetworkServers().clear();

                            for (JsonElement server : servers) {
                                String name = server.getAsJsonObject().get("name").getAsString();
                                ServerType type;
                                String address = server.getAsJsonObject().get("address").getAsString();
                                if (name.contains("GAME")) {
                                    type = ServerType.GAME;
                                } else {
                                    type = ServerType.LOBBY;
                                }
                                addServer(new CoreServer(type, name, address));
                            }
                        }
                    }
                }, channels.toArray(myArray));
            }, "jedisSub").start();
        }
    }
}
