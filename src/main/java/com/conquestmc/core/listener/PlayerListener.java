package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.model.Rank;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
            }
            else {
                conquestPlayer = new ConquestPlayer(pl.getUniqueId(), pl.getName());
            }

            plugin.getPlayers().put(pl.getUniqueId(), conquestPlayer);
            plugin.logPlayer(conquestPlayer);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player pl = event.getPlayer();

        Document d = new Document("_id", pl.getUniqueId().toString());
        Document player = plugin.getPlayer(pl.getUniqueId()).getMongoObject();

        if (plugin.findPlayer(pl.getUniqueId()) == null) {
            plugin.getPlayerCollection().insertOne(player);
        }
        else {
            plugin.getPlayerCollection().replaceOne(d, player);
        }
        plugin.remPlayer(plugin.getPlayer(pl));
        plugin.getPlayers().remove(pl.getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ConquestPlayer conquestPlayer = plugin.getPlayer(player);
        Rank rank = conquestPlayer.getRank();
        String format;
        switch (rank) {
            case TRAIL:
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
