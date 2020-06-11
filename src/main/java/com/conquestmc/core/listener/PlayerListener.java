package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.model.PermissionRegistry;
import com.conquestmc.core.model.Rank;

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

        plugin.getPerms().put(pl.getUniqueId(), pl.addAttachment(plugin));
        plugin.getPlayerManager().getOrInit(pl.getUniqueId());
        try {
            for (String s : PermissionRegistry.valueOf(plugin.getPlayerManager().getConquestPlayer(pl.getUniqueId()).getRank().name()).getPermissions()) {
                plugin.getPerms().get(pl.getUniqueId()).setPermission(s, true);
            }
        } catch (IllegalArgumentException ignored) {

        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player pl = event.getPlayer();

        plugin.getPlayerManager().pushPlayer(pl.getUniqueId());

        plugin.remPlayer(plugin.getPlayer(pl));
        plugin.getPlayerManager().removePlayer(pl.getUniqueId());

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
