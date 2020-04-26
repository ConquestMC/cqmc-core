package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.model.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private CorePlugin plugin;

    public PlayerListener(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();
        if (plugin.getPlayer(pl) == null) {
            ConquestPlayer conquestPlayer = new ConquestPlayer(pl.getUniqueId(), pl.getName());
            plugin.getPlayerDao().insertPlayer(pl.getUniqueId().toString(), pl.getName(), Rank.TUTORIAL_1.name(), System.currentTimeMillis());
            conquestPlayer.setCoins(plugin.getPlayerDao().getCoins(pl.getUniqueId().toString()));
            conquestPlayer.setRank(Rank.valueOf(plugin.getPlayerDao().getRank(pl.getUniqueId().toString())));

            plugin.getPlayers().put(pl.getUniqueId(), conquestPlayer);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player pl = event.getPlayer();
        plugin.getPlayerDao().setRank(pl.getUniqueId().toString(), plugin.getPlayer(pl.getUniqueId()).getRank().name());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ConquestPlayer conquestPlayer = plugin.getPlayer(player);
        Rank rank = conquestPlayer.getRank();

        event.setFormat(rank.getPrefix() + " " + player.getName() + ChatColor.GRAY + " > "
                + ChatColor.RESET
                + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
    }
}
