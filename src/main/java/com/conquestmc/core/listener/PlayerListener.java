package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.bossbar.BossBarManager;
import com.conquestmc.core.model.Rank;
import com.conquestmc.core.util.ChatUtil;
import com.conquestmc.foundation.API;
import com.conquestmc.foundation.CorePlayer;
import com.conquestmc.foundation.player.FProfile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class PlayerListener implements Listener {

    private CorePlugin plugin;

    private String[] joinMessages = new String[]{
            "&8&l[&a&l+&8&l] {rank} {nameColor}{name} &chas joined the game",
            "&8&l[&a&l+&8&l] {rank} {nameColor}{name} &dhas joined the game",
            "&8&l[&a&l+&8&l] {rank} {nameColor}{name} &6has joined the game",
            "&8&l[&a&l+&8&l] {rank} {nameColor}{name} &6&lhas joined the game"
    };

    public PlayerListener(CorePlugin plugin) {
        this.plugin = plugin;
    }
    public TextComponent getChatFormat(CorePlayer player, String name, String message) {

        FProfile mainProfile = player.getProfile("main");
        Rank rank = plugin.getServerConfig().getRankByName(mainProfile.getString("rank"));

        //String p = rank.getName().equalsIgnoreCase("none") ? "" : rank.getPrefix();


        TextComponent prefix = new TextComponent(rank.getName().equalsIgnoreCase("none") ? "" : ChatUtil.color(rank.getPrefix()));
        TextComponent username = new TextComponent(ChatUtil.color(" " + mainProfile.getString("nameColor") + name));
        TextComponent split = new TextComponent(" | ");
        TextComponent msg = new TextComponent(ChatUtil.color(mainProfile.getString("rank").equalsIgnoreCase("none") ? ChatColor.GRAY + message : ChatColor.WHITE + message));

        prefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.color(rank.getPrefix()) + "\n" + getStaffOnHover(player)).create()));
        username.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.color(mainProfile.getString("nameColor") + name))
                .append("\n")
                .append("\n")
                .append(ChatUtil.color("&6&lRank " + (rank.getName().equalsIgnoreCase("none") ? "None" : rank.getPrefix())))
                .append("\n")
                .append(ChatUtil.color("&6&lCurrency"))
                .append("\n")
                .append(ChatUtil.color("  &6⇾ &eDrachma: &f" + mainProfile.getInteger("drachma")))
                .append("\n")
                .append(ChatUtil.color("  &6⇾ &eConquest Points: &f" + mainProfile.getInteger("points")))
                .append("\n")
                .append(ChatUtil.color("&6&lFriends &f" + ((List<String>) mainProfile.getObject("friends")).size()))
                .append("\n\n")
                .append((ChatUtil.color("&2&l☛ &a&lClick to view profile &2&l☚"))).create()));
        split.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        return new TextComponent(prefix, username, split, msg);
    }

    private String getStaffOnHover(CorePlayer player) {
        FProfile permProfile = player.getProfile("main");
        Rank rank = plugin.getServerConfig().getRankByName(permProfile.getString("rank"));

        if (player.isStaff()) {
            return ChatUtil.color("&6Staff");
        } else if (player.isDonor()) {
            return ChatUtil.color("&5Donor");
        } else {
            return ChatUtil.color("&7Default");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        ChatUtil.clearPlayerChat(p);
        event.setJoinMessage("");
        plugin.getBossBarManager().showBossBar(p);

        //this is where we need to do some work
        CorePlayer player = (CorePlayer) API.getUserManager().findByUniqueId(p.getUniqueId());

        if (player == null) {
            System.out.println("Null");
        }

        new BukkitRunnable() {
            public void run() {
                CorePlayer player = (CorePlayer) API.getUserManager().findByUniqueId(p.getUniqueId());
            }
        }.runTaskLater(plugin, 2L);

        FProfile mainProfile = player.getProfile("main");

        Rank rank = plugin.getServerConfig().getRankByName(mainProfile.getString("rank"));
        String nameColor = mainProfile.getString("nameColor");

        plugin.applyPermissions(p, rank);
        mainProfile.set("lastLogin", ChatUtil.formatDate.format(new Date().getTime()));
        player.update();
        if (rank.getName().equalsIgnoreCase("none")) {
            p.setPlayerListName(ChatUtil.color(nameColor + p.getName() + "   "));
        } else {
            p.setPlayerListName(ChatUtil.color(rank.getPrefix() +" " + nameColor + p.getName() + "   "));
        }

        if (rank.getPermissions().contains("rank.staff")) {
            Bukkit.broadcastMessage(ChatUtil.color(joinMessages[0]
                    .replace("{rank}", rank.getPrefix())
                    .replace("{nameColor}", nameColor)
                    .replace("{name}", p.getName())));
        } else {
            if (rank.getName().equalsIgnoreCase("content")) {
                Bukkit.broadcastMessage(ChatUtil.color(joinMessages[1]
                        .replace("{rank}", "Content Creator")
                        .replace("{nameColor}", nameColor)
                        .replace("{name}", p.getName())));
            }
            if (rank.getName().equalsIgnoreCase("king")) {
                Bukkit.broadcastMessage(ChatUtil.color(joinMessages[2]
                        .replace("{rank}", rank.getPrefix())
                        .replace("{nameColor}", nameColor)
                        .replace("{name}", p.getName())));
            }
            if (rank.getName().equalsIgnoreCase("emperor")) {
                Bukkit.broadcastMessage(ChatUtil.color(joinMessages[3]
                        .replace("{rank}", rank.getPrefix())
                        .replace("{nameColor}", nameColor)
                        .replace("{name}", p.getName())));
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CorePlayer conquestPlayer = (CorePlayer) API.getUserManager().findByUniqueId(player.getUniqueId());

        event.setCancelled(true); //TODO ADD CHECK TO MAKE SURE PLAYER IS NOT MUTED ---------------
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.spigot().sendMessage(getChatFormat(conquestPlayer, player.getName(), event.getMessage()));
        }
    }

    @EventHandler
    public void onVanishJoin(PlayerJoinEvent event) {
        CorePlayer player = (CorePlayer) API.getUserManager().findByUniqueId(event.getPlayer().getUniqueId());
        FProfile mainProfile = player.getProfile("main");

        if (mainProfile.getBoolean("vanished")) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (!pl.hasPermission("group.admin")) {
                    pl.hidePlayer(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        CorePlayer corePlayer = (CorePlayer) API.getUserManager().findByUniqueId(e.getPlayer().getUniqueId());
        FProfile mainProfile = corePlayer.getProfile("main");
        try {
            String loggedOutAt = ChatUtil.formatDate.format(new Date().getTime());
            Date d1 = ChatUtil.formatDate.parse(mainProfile.getString("lastLogin"));
            Date d2 = ChatUtil.formatDate.parse(loggedOutAt);

            long diff = d2.getTime() - d1.getTime(); //Milliseconds
            long seconds = diff / 1000 % 60; //Seconds

            mainProfile.set("playtime", mainProfile.getLong("playtime") + seconds);
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        plugin.getBossBarManager().removeBar(e.getPlayer());
    }
}
