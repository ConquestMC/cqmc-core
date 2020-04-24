package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.model.ConquestPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
            plugin.getPlayers().put(pl.getUniqueId(), conquestPlayer);
        }
    }
}
