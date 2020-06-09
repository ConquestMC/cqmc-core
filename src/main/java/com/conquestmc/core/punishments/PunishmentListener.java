package com.conquestmc.core.punishments;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

@RequiredArgsConstructor
public class PunishmentListener implements Listener {

    private final PunishmentManager punishmentManager;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (punishmentManager.isMuted(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You are currently muted! You may not speak at this time.");
        }
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if (punishmentManager.isGameBanned(uuid)) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "You have been banned from ConquestMC for: <reason>");
        }
    }
}
