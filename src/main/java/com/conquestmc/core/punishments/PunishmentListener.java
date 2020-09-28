package com.conquestmc.core.punishments;

import com.conquestmc.core.server.ServerManager;
import com.conquestmc.core.server.ServerMessages;
import com.conquestmc.core.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
            event.getPlayer().sendMessage(ServerMessages.PUNISH_PREFIX.getPrefix()+ ChatUtil.color("&cYou are currently muted! You may not speak at this time."));
        }
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if (punishmentManager.isGameBanned(uuid)) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ServerMessages.PUNISH_PREFIX.getPrefix() + ChatUtil.color("You have been banned from ConquestMC for: <reason>")); //TODO switch to format specified in Server Planning (Ask kyle if trouble finding)
        }
    }
}
