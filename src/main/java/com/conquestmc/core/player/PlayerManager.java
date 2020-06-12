package com.conquestmc.core.player;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.InsertOneOptions;
import lombok.Getter;
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

    @Getter
    private Map<UUID, ConquestPlayer> players = Maps.newHashMap();

    public PlayerManager(MongoCollection<Document> playerCollection) {
        this.playerCollection = playerCollection;
    }

    public CompletableFuture<ConquestPlayer> getOrInitPromise(UUID uuid) {
        CompletableFuture<ConquestPlayer> promise = new CompletableFuture<>();

        playerCollection.find(eq("uuid", uuid.toString())).first((document, throwable) -> {
            if (document == null) {
                System.out.println("null doc");
                ConquestPlayer temp = new ConquestPlayer(uuid);
                players.put(uuid, temp);
                promise.complete(temp);
                insertPlayer(uuid);
            }
            else {
                promise.complete(new ConquestPlayer(uuid, document));
            }
        });
        return promise;
    }
    public void removePlayer(UUID uuid) {
        this.players.remove(uuid);
    }

    public void pushPlayer(UUID uuid) {

        cachePlayer(uuid);

        playerCollection.findOneAndReplace(eq("uuid", uuid.toString()), players.get(uuid).getMongoObject(), (document, throwable) -> {
            System.out.println(document == null);
        });
    }

    public void insertPlayer(UUID uuid) {
        playerCollection.insertOne(players.get(uuid).getMongoObject(), (aVoid, throwable) -> {

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
