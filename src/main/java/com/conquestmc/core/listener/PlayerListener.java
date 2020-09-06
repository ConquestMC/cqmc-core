package com.conquestmc.core.listener;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.event.PlayerLoadedEvent;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.player.DonationRank;
import com.conquestmc.core.player.Rank;
import com.conquestmc.core.player.StaffRank;
import com.conquestmc.core.util.OldSounds;
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
import redis.clients.jedis.Jedis;

import java.util.Arrays;
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

    @EventHandler
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
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Could not load player data! Contact an administrator.");
            return;
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

    public TextComponent getChatFormat(ConquestPlayer player, String name, String message) {
        TextComponent prefix = new TextComponent(player.getPrefixedRank().getName().equalsIgnoreCase("none") ? "" : player.getPrefixedRank().getPrefix());
        TextComponent username = new TextComponent(ChatColor.translateAlternateColorCodes('&', player.getNameColor() + name));
        TextComponent split = new TextComponent(" | ");
        TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&', player.getPrefixedRank().getName().equalsIgnoreCase("none") ? ChatColor.GRAY + message : ChatColor.WHITE + message));

        prefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(player.getPrefixedRank().getPrefix() + "\n" + getStaffOnHover(player)).create()));
        username.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                ChatColor.translateAlternateColorCodes('&', player.getNameColor() + name + "\n\n"
                        + "&6&lRank " + prefix + "\n"
                        + "&6&lServer " + "&fHub-1") //TODO---------------
                        + "&6&lCurrency\n"
                        + "  &6⇾ &eDrachma: &f" + player.getCoins() + "\n"
                        + "  &6⇾ &eConquest Points: &f" + player.getPoints() + "\n"
                        + "&6&lCompetitive Tier: &f" + player.getCompetitiveRankName() + "\n"
                        + "&6&lFriends " + player.getFriends().size() + "\n\n"
                        + "&2&l☛ &a&lClick to view profile &2&l☚") //TODO-----------
                .create()));
        split.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        return new TextComponent(prefix, username, split, msg);
    }

    private String getStaffOnHover(ConquestPlayer player) {
        if (player.getPrefixedRank() instanceof StaffRank) {
            return ChatColor.translateAlternateColorCodes('&', "&6Staff");
        } else if (player.getPrefixedRank() instanceof DonationRank) {
            return ChatColor.translateAlternateColorCodes('&', "&5Donor");
        } else {
            return ChatColor.translateAlternateColorCodes('&', "&7Default");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage("");
        p.playSound(p.getLocation(), OldSounds.LEVEL_UP.playSound(),1,1);
        p.playSound(p.getLocation(), OldSounds.NOTE_BASS.playSound(),1,1);

        plugin.getPlayerManager().getOrInitPromise(p.getUniqueId()).whenComplete((newConquestPlayer, e) -> {
            newConquestPlayer.setKnownName(p.getName());
            for (Rank rank : newConquestPlayer.getRanks()) {
                String[] arr = new String[rank.getPermissions().size()];
                plugin.getPlayerManager().givePermissions(p, rank.getPermissions().toArray(arr));
            }
            Rank prefixed = newConquestPlayer.getPrefixedRank();
            String nameColor = newConquestPlayer.getNameColor();
            p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', prefixed.getPrefix() + " " + nameColor + p.getName() + "   "));

            if (prefixed instanceof StaffRank) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', joinMessages[0]
                        .replace("{rank}", prefixed.getPrefix())
                        .replace("{nameColor}", nameColor)
                        .replace("{name}", p.getName())));
            } else {
                if (prefixed.getName().equalsIgnoreCase("content")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', joinMessages[1]
                            .replace("{rank}", "Content Creator")
                            .replace("{nameColor}", nameColor)
                            .replace("{name}", p.getName())));
                }
                if (prefixed.getName().equalsIgnoreCase("king")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', joinMessages[2]
                            .replace("{rank}", prefixed.getPrefix())
                            .replace("{nameColor}", nameColor)
                            .replace("{name}", p.getName())));
                }
                if (prefixed.getName().equalsIgnoreCase("emperor")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', joinMessages[3]
                            .replace("{rank}", prefixed.getPrefix())
                            .replace("{nameColor}", nameColor)
                            .replace("{name}", p.getName())));
                }
            }
            plugin.getPlayerManager().getPlayers().put(p.getUniqueId(), newConquestPlayer);
            plugin.logPlayer(p);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player pl = event.getPlayer();
        event.setQuitMessage("");
        //plugin.getPlayerManager().pushPlayer(pl.getUniqueId());

        plugin.getPlayerManager().updatePlayer(pl.getUniqueId()).whenComplete((b, throwable) -> {
            if (throwable != null) {
                System.err.println(Arrays.toString(throwable.getStackTrace()));
            }
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try (Jedis j = plugin.getJedisPool().getResource()) {
                    j.del("status." + pl.getUniqueId().toString());
                }
            });
        });

        plugin.remPlayer(pl);
        plugin.getPlayerManager().removePermissions(pl);
        plugin.getPlayerManager().removePlayer(pl.getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ConquestPlayer conquestPlayer = plugin.getPlayer(player);

        event.setCancelled(true); //TODO ADD CHECK TO MAKE SURE PLAYER IS NOT MUTED ---------------
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.spigot().sendMessage(getChatFormat(conquestPlayer, player.getName(), event.getMessage()));
        }
    }

    @EventHandler
    public void onVanishJoin(PlayerJoinEvent event) {
        plugin.getPlayerManager().getOrInitPromise(event.getPlayer().getUniqueId()).whenComplete(((conquestPlayer, throwable) -> {
            if (conquestPlayer.isVanished()) {

                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (!pl.hasPermission("group.admin")) {
                        pl.hidePlayer(event.getPlayer());
                    }
                }
            }
        }));
    }
}
