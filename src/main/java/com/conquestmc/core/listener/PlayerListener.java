package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.model.PermissionRegistry;
import com.conquestmc.core.model.Rank;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

public class PlayerListener implements Listener {

    private CorePlugin plugin;

    public PlayerListener(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();

        if (plugin.getPlayer(pl) == null) {

            Document doc = plugin.findPlayer(pl.getUniqueId());
            ConquestPlayer conquestPlayer;

            if (doc != null) {
                conquestPlayer = new ConquestPlayer(pl.getUniqueId(), doc);
            } else {
                conquestPlayer = new ConquestPlayer(pl.getUniqueId(), pl.getName());
                plugin.getPlayerCollection().insertOne(conquestPlayer.getMongoObject());
            }

            plugin.getPlayers().put(pl.getUniqueId(), conquestPlayer);
            plugin.logPlayer(conquestPlayer);
            plugin.getPerms().put(pl.getUniqueId(), pl.addAttachment(plugin));
            try {
                for (String s : PermissionRegistry.valueOf(conquestPlayer.getRank().name()).getPermissions()) {
                    plugin.getPerms().get(pl.getUniqueId()).setPermission(s, true);
                }
            } catch (IllegalArgumentException ignored) {

            }
        }
        else {
            System.out.println("Not null");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player pl = event.getPlayer();

        Document player = plugin.getPlayers().get(pl.getUniqueId()).getMongoObject();
        System.out.println("Mongo object is: " + player.toJson());

        UpdateResult result = plugin.getPlayerCollection().replaceOne(eq("uuid", pl.getUniqueId().toString()), player);

        if (result.wasAcknowledged()) {
            System.out.println("Acknowledged, changed: " + result.getModifiedCount());
        }
        else {
            System.out.println("Failed to replace");
        }

        plugin.remPlayer(plugin.getPlayer(pl));
        plugin.getPlayers().remove(pl.getUniqueId());

        pl.removeAttachment(plugin.getPerms().get(pl.getUniqueId()));
        plugin.getPerms().remove(pl.getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ConquestPlayer conquestPlayer = plugin.getPlayer(player);
        Rank rank = conquestPlayer.getRank();
        String format;
        switch (rank) {
            case TRIAL:
            case MOD:
            case ADMIN:
            case OWNER:
            case MANAGER:
                format = rank.getPrefix() + player.getName() + ChatColor.WHITE + " : " + ChatColor.translateAlternateColorCodes('&', event.getMessage());
                break;
            default:
                format = ChatColor.YELLOW + "" + conquestPlayer.getPoints() + " " + rank.getPrefix() + player.getName() + ChatColor.WHITE + " : " + ChatColor.translateAlternateColorCodes('&', event.getMessage());
                break;
        }

        event.setFormat(format);
    }
}
