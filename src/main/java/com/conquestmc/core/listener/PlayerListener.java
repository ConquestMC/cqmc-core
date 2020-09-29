package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    /*@EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        int slept = 0;
        while (plugin.getJedisPool().getResource().setnx("status." + uuid.toString(), "online") != 1 && slept < 15) {
            try {
                Thread.sleep(10);
                slept++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (slept == 15) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatUtil.color(ServerMessages.SERVER_PREFIX.getPrefix() + "&cCould not load player data! Contact an administrator."));
            return;
        }

        /*while (plugin.getJedisPool().getResource().setnx("status." + uuid.toString(), ) != 1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        plugin.getPlayerManager().getOrInitPromise(uuid).whenComplete(((conquestPlayer, throwable) -> {
            if (throwable != null) {
                System.err.println(throwable);
                return;
            }
            System.out.println(conquestPlayer.getBukkitPlayer().getName() + " Joined with rank: " + conquestPlayer.getPrefixedRank().getPrefix()); //todo proper logging system
        }));
        Bukkit.getPluginManager().callEvent(new PlayerLoadedEvent(plugin.getPlayer(uuid)));
    }*/

    public TextComponent getChatFormat(CorePlayer player, String name, String message) {

        FProfile permProfile = player.getProfile("permissions");
        Rank rank = plugin.getServerConfig().getRankByName(permProfile.getString("rank"));

        FProfile cosmetic = player.getProfile("cosmetics");
        FProfile core = player.getProfile("core");

        String p = rank.getName().equalsIgnoreCase("none") ? "" : rank.getPrefix();


        TextComponent prefix = new TextComponent(ChatUtil.color(p));
        TextComponent username = new TextComponent(ChatUtil.color(" " + cosmetic.getString("nameColor") + name));
        TextComponent split = new TextComponent(" | ");
        TextComponent msg = new TextComponent(ChatUtil.color(permProfile.getString("rank").equalsIgnoreCase("none") ? ChatColor.GRAY + message : ChatColor.WHITE + message));

        prefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(rank.getPrefix() + "\n" + getStaffOnHover(player)).create()));
        username.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtil.color(cosmetic.getString("nameColor") + name))
                .append("\n")
                .append("\n")
                .append(ChatUtil.color("&6&lRank " + rank.getPrefix()))
                .append("\n")
                .append(ChatUtil.color("&6&lCurrency"))
                .append("\n")
                .append(ChatUtil.color("  &6⇾ &eDrachma: &f" + core.getDouble("drachma")))
                .append("\n")
                .append(ChatUtil.color("  &6⇾ &eConquest Points: &f" + core.getDouble("points")))
                .append("\n")
                .append(ChatUtil.color("&6&lFriends " + ((List<String>) core.getObject("friends")).size()))
                .append("\n")
                .append((ChatUtil.color("&2&l☛ &a&lClick to view profile &2&l☚"))).create()));
        split.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        return new TextComponent(prefix, username, split, msg);
    }

    private String getStaffOnHover(CorePlayer player) {
        FProfile permProfile = player.getProfile("permissions");
        Rank rank = plugin.getServerConfig().getRankByName(permProfile.getString("rank"));

        if (rank.getPermissions().contains("staff.rank")) {
            return ChatUtil.color("&6Staff");
        } else if (rank.getPermissions().contains("donator.rank")) {
            return ChatUtil.color("&5Donor");
        } else {
            return ChatUtil.color("&7Default");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage("");

        //this is where we need to do some work
        CorePlayer player = (CorePlayer) API.getUserManager().findByUniqueId(p.getUniqueId());

        if (player == null) {
            System.out.println("Null");
        }

        new BukkitRunnable() {
            public void run() {
                CorePlayer player = (CorePlayer) API.getUserManager().findByUniqueId(p.getUniqueId());
                System.out.println("player is " + (player == null));
            }
        }.runTaskLater(plugin, 2L);

        if (player.getProfile("cosmetics") == null) {
            FProfile cosmetics = new FProfile("cosmetics", Maps.newHashMap());
            cosmetics.set("unlockedCosmetics", new ArrayList<String>());
            cosmetics.set("nameColor", "&7");

            player.getAllProfiles().add(cosmetics);
        }

        if (player.getProfile("permissions") == null) {
            FProfile perms = new FProfile("permissions", Maps.newHashMap());
            perms.set("rank", "none");
            perms.set("permissions", new ArrayList<String>());
            player.getAllProfiles().add(perms);
        }

        FProfile profile = player.getProfile("permissions");

        Rank rank = plugin.getServerConfig().getRankByName(profile.getString("rank"));
        FProfile cosmetics = player.getProfile("cosmetics");
        String nameColor = cosmetics.getString("nameColor");

        plugin.applyPermissions(p, rank);

        p.setPlayerListName(ChatUtil.color(rank.getPrefix() + " " + nameColor + p.getName() + "   "));
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
        FProfile permProfile = player.getProfile("settings");

        if (permProfile == null) {
            permProfile = new FProfile("settings", Maps.newHashMap());
            permProfile.set("vanished", false);
        }

        if (permProfile.getBoolean("vanished")) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (!pl.hasPermission("group.admin")) {
                    pl.hidePlayer(event.getPlayer());
                }
            }
        }
    }
}