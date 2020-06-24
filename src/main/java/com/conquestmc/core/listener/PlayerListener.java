package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.event.PlayerLoadedEvent;
import com.conquestmc.core.model.ConquestPlayer;

import com.conquestmc.core.model.SimpleScoreboard;
import com.conquestmc.core.player.Rank;

import com.conquestmc.core.player.StaffRank;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.UUID;

public class PlayerListener implements Listener {

    private CorePlugin plugin;

    private String[] joinMessages = new String[]{
            "  &8&l[&2&l+&8&l] &c{rank} {name} has joined the game",
            "  &8&l[&2&l+&8&l] &5{rank} {name} has joined the game",
            "  &8&l[&2&l+&8&l] &6{rank} {name} has joined the game",
            "  &8&l[&2&l+&8&l] &6&l{rank} {name} has joined the game"
    };

    public PlayerListener(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        while (!plugin.getJedisPool().getResource().get("status." + uuid.toString()).equals(String.valueOf(Bukkit.getServer().getPort()))
                || plugin.getJedisPool().getResource().setnx("status." + uuid.toString(), String.valueOf(Bukkit.getServer().getPort())) != 1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*while (plugin.getJedisPool().getResource().setnx("status." + uuid.toString(), ) != 1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        plugin.getPlayerManager().getOrInitPromise(uuid).whenComplete(((conquestPlayer, throwable) -> {
            if (throwable != null) {
                System.err.println(throwable);
                return;
            }
            System.out.println(conquestPlayer.getBukkitPlayer().getName() + " Joined with rank: " + conquestPlayer.getPrefixedRank().getPrefix());
        }));
        Bukkit.getPluginManager().callEvent(new PlayerLoadedEvent(plugin.getPlayer(uuid)));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage("");

        plugin.getPlayerManager().getOrInitPromise(p.getUniqueId()).whenComplete((newConquestPlayer, e) -> {
            newConquestPlayer.setKnownName(p.getName());
            for (Rank rank : newConquestPlayer.getRanks()) {
                String[] arr = new String[rank.getPermissions().size()];
                plugin.getPlayerManager().givePermissions(p, rank.getPermissions().toArray(arr));
            }

            Rank prefixed = newConquestPlayer.getPrefixedRank();
            if (prefixed instanceof StaffRank) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', joinMessages[0]
                        .replace("{rank}", WordUtils.capitalizeFully(prefixed.getName()))
                        .replace("{name}", p.getName())));
            } else {
                if (prefixed.getName().equalsIgnoreCase("content")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', joinMessages[1]
                            .replace("{rank}", "Content Creator")
                            .replace("{name}", p.getName())));
                }
                if (prefixed.getName().equalsIgnoreCase("king")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', joinMessages[2]
                            .replace("{rank}", WordUtils.capitalizeFully(prefixed.getName()))
                            .replace("{name}", p.getName())));
                }
                if (prefixed.getName().equalsIgnoreCase("emperor")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', joinMessages[3]
                            .replace("{rank}", WordUtils.capitalizeFully(prefixed.getName()))
                            .replace("{name}", p.getName())));
                }
            }
            plugin.getPlayerManager().getPlayers().put(p.getUniqueId(), newConquestPlayer);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player pl = event.getPlayer();
        //plugin.getPlayerManager().pushPlayer(pl.getUniqueId());

        plugin.getPlayerManager().updatePlayer(pl.getUniqueId()).whenComplete((b, throwable) -> {
            if (throwable != null) {
                System.err.println(Arrays.toString(throwable.getStackTrace()));
            }
            try (Jedis j = plugin.getJedisPool().getResource()) {
                j.del("status." + pl.getUniqueId().toString());
            }
        });

        plugin.remPlayer(plugin.getPlayer(pl));
        plugin.getPlayerManager().removePermissions(pl);
        plugin.getPlayerManager().removePlayer(pl.getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ConquestPlayer conquestPlayer = plugin.getPlayer(player);
        Rank prefixedRank = conquestPlayer.getPrefixedRank();

        String chat = prefixedRank.getName().equalsIgnoreCase("none") ? ChatColor.GRAY + event.getMessage() : ChatColor.WHITE + event.getMessage();
        String prefix = prefixedRank.getName().equalsIgnoreCase("none") ? "" : prefixedRank.getPrefix() + ChatColor.GRAY + " ⎥ ";
        String format = prefix + ChatColor.YELLOW + player.getName() + " " + chat;
        event.setFormat(format);
    }
}
