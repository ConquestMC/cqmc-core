package com.conquestmc.core.player;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.async.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mongodb.client.model.Filters.eq;

public class PlayerManager {

    private MongoCollection<Document> playerCollection;
    private Map<UUID, ConquestPlayer> players = Maps.newHashMap();

    public PlayerManager(MongoCollection<Document> playerCollection) {
        this.playerCollection = playerCollection;
    }

    public void getOrInit(UUID uuid) {
        //TODO first we would check redis cache, for now just gonna pull from DB or init

        try (Jedis j = CorePlugin.getInstance().getJedisPool().getResource()) {
            List<String> cachedPlayerJson = j.lrange("playerCache", 0, -1);

            for (String json : cachedPlayerJson) {
                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();

                if (obj.get("uuid").getAsString().equalsIgnoreCase(uuid.toString())) {
                    //PLAYER
                }
            }
        }

        CompletableFuture<ConquestPlayer> promise = new CompletableFuture<>();

        playerCollection.find(eq("uuid", uuid.toString())).first((document, throwable) -> {
            if (document == null) {
                System.out.println("NULL Document");
                promise.complete(null);
            } else {
                players.put(uuid, new ConquestPlayer(uuid, document));
                System.out.println("Player found from db: " + document.toJson());
                promise.complete(players.get(uuid));
            }
        });
        try {
            if (promise.get() == null) {
                this.players.put(uuid, new ConquestPlayer(uuid, Bukkit.getPlayer(uuid).getName()));
                playerCollection.insertOne(players.get(uuid).getMongoObject(), ((aVoid, throwable) -> {
                    System.out.println("Inserting default player as none was found");
                }));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void removePlayer(UUID uuid) {
        this.players.remove(uuid);
    }

    public void pushPlayer(UUID uuid) {
        System.out.println(players.get(uuid) == null);
        System.out.println(players.get(uuid).getMongoObject() == null);
        playerCollection.findOneAndReplace(eq("uuid", uuid.toString()), players.get(uuid).getMongoObject(), (document, throwable) -> {
            System.out.println(document == null);
        });
    }

    public void pushPlayer(Player player) {
        pushPlayer(player.getUniqueId());
    }

    public ConquestPlayer getConquestPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public void cachePlayer(UUID uuid) {
        try (Jedis j = CorePlugin.getInstance().getJedisPool().getResource()) {
            j.lpush("playerCache", players.get(uuid).getMongoObject().toJson());
        }
    }
}
