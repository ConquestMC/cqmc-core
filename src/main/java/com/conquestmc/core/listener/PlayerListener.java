package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;

import com.conquestmc.core.player.Rank;
import com.conquestmc.core.player.StaffRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PlayerListener implements Listener {

    private CorePlugin plugin;

    public PlayerListener(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        try {
            plugin.getPlayerManager().getOrInitPromise(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Could not load player data");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getPlayerManager().getOrInitPromise(p.getUniqueId()).whenComplete((newConquestPlayer, e) -> {
                newConquestPlayer.setKnownName(p.getName());
                for (Rank rank : newConquestPlayer.getRanks()) {
                    String[] arr = new String[rank.getPermissions().size()];
                    plugin.getPlayerManager().givePermissions(p, rank.getPermissions().toArray(arr));
                }
                plugin.getPlayerManager().getPlayers().put(p.getUniqueId(), newConquestPlayer);
            });
        }, 3L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player pl = event.getPlayer();

        System.out.println("CALLED");

        plugin.getPlayerManager().pushPlayer(pl.getUniqueId());

        plugin.remPlayer(plugin.getPlayer(pl));
        plugin.getPlayerManager().removePlayer(pl.getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ConquestPlayer conquestPlayer = plugin.getPlayer(player);
        Rank prefixedRank = conquestPlayer.getPrefixedRank();


        String format;
        switch (prefixedRank.getName()) {
            case "trial":
            case "mod":
            case "admin":
            case "dev":
            case "owner":
            case "manager":
                format = prefixedRank.getPrefix() + player.getName() + ChatColor.WHITE + " : " + ChatColor.translateAlternateColorCodes('&', event.getMessage());
                break;
            default:
                format = ChatColor.YELLOW + "" + conquestPlayer.getPoints() + " " + prefixedRank.getPrefix() + player.getName() + ChatColor.WHITE + " : " + ChatColor.translateAlternateColorCodes('&', event.getMessage());
                break;
        }
        event.setFormat(format);
    }
}
