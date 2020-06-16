package com.conquestmc.core.player;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import com.google.common.collect.Maps;
import com.mongodb.async.client.MongoCollection;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

public class PlayerManager {

    private MongoCollection<Document> playerCollection;

    @Getter
    private Map<UUID, ConquestPlayer> players = Maps.newHashMap();

    @Getter
    private Map<UUID, PermissionAttachment> permissionAttachments = Maps.newHashMap();

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
        playerCollection.findOneAndReplace(eq("uuid", uuid.toString()), players.get(uuid).getMongoObject(), (document, throwable) -> {
            System.out.println(document == null);
        });
    }

    public void insertPlayer(UUID uuid) {
        playerCollection.insertOne(players.get(uuid).getMongoObject(), (aVoid, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        });
    }

    public void pushPlayer(Player player) {
        pushPlayer(player.getUniqueId());
    }

    public ConquestPlayer getConquestPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public void givePermission(Player player, String perm) {
        PermissionAttachment attachment = permissionAttachments.getOrDefault(player.getUniqueId(), player.addAttachment(CorePlugin.getInstance()));
        attachment.setPermission(perm, true);
        this.permissionAttachments.put(player.getUniqueId(), attachment);
    }

    public void givePermissions(Player player, String... perms) {
        PermissionAttachment attachment = permissionAttachments.getOrDefault(player.getUniqueId(), player.addAttachment(CorePlugin.getInstance()));
        for (String p : perms) {
            attachment.setPermission(p, true);
        }
        permissionAttachments.put(player.getUniqueId(), attachment);
    }

    public void removePermissions(Player player) {
        PermissionAttachment attachment = this.permissionAttachments.get(player.getUniqueId());
        player.removeAttachment(attachment);
    }
}
