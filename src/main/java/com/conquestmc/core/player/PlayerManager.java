package com.conquestmc.core.player;

import com.conquestmc.core.model.ConquestPlayer;
import com.google.common.collect.Maps;
import com.mongodb.async.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

        CompletableFuture<ConquestPlayer> promise = new CompletableFuture<>();

        playerCollection.find(eq("uuid", uuid.toString())).first((document, throwable) -> {
            if (document == null) {
                promise.complete(null);
                return;
            }
            players.put(uuid, new ConquestPlayer(uuid, document));
            System.out.println("Player found from db: " + document.toJson());
            promise.complete(players.get(uuid));
        });
        try {
            if (promise.get() == null) {
                this.players.put(uuid, new ConquestPlayer(uuid, Bukkit.getPlayer(uuid).getName()));
                playerCollection.insertOne(players.get(uuid).getMongoObject(), ((aVoid, throwable) -> {
                    return;
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
        playerCollection.findOneAndReplace(eq("uuid", uuid.toString()), players.get(uuid).getMongoObject(), (document, throwable) -> System.out.println("Player document: " + document.toJson() + " was replaced.."));
    }

    public void pushPlayer(Player player) {
        pushPlayer(player.getUniqueId());
    }

    public ConquestPlayer getConquestPlayer(UUID uuid) {
        return players.get(uuid);
    }
}
