package com.conquestmc.core;

import com.conquestmc.core.bossbar.BossBarManager;
import com.conquestmc.core.command.*;
import com.conquestmc.core.command.framework.CCommand;
import com.conquestmc.core.command.framework.CommandInfo;
import com.conquestmc.core.command.framework.CommandPreProcess;
import com.conquestmc.core.config.ConfigManager;
import com.conquestmc.core.config.MainConfig;
import com.conquestmc.core.listener.PlayerListener;
import com.conquestmc.core.listener.ProfileDefaultListener;
import com.conquestmc.core.listener.SignListener;
import com.conquestmc.core.model.Rank;
import com.conquestmc.core.punishments.PunishmentCommand;
import com.conquestmc.core.punishments.PunishmentHistoryCommand;
import com.conquestmc.core.punishments.PunishmentListener;
import com.conquestmc.core.punishments.PunishmentManager;
import com.conquestmc.core.server.ServerManager;
import com.conquestmc.foundation.API;
import com.conquestmc.foundation.CorePlayer;
import com.conquestmc.foundation.player.FProfile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CorePlugin extends JavaPlugin {

    @Getter
    private ConfigManager<MainConfig> serverConfigManager = new ConfigManager<>(this, "config.json", MainConfig.class);
    @Getter
    private MainConfig serverConfig;

    @Getter
    private static CorePlugin instance;

    @Getter
    private JedisPool jedisPool;

    @Getter
    private Map<UUID, PermissionAttachment> perms = Maps.newHashMap();

    private PunishmentManager punishmentManager;

    @Getter
    private Map<String, CCommand> commandMap = Maps.newHashMap();

    private BossBarManager bossBarManager;

    /*
            <aesthetic-update changelist> <2020/09/21>

    - Created registerCommands() method in CorePlugin.java to clean up onEnable()
    - Created ChatUtil which should now always be used to convert color codes
    - Deleted ServerManager.java as it is empty and unused
    - Deleted TrophyListener.java as it is empty and unused
    - Deleted PardonCommand.java as it is empty and unused
    - Created ServerManager.java again to store prefix's & logging method
    - Updated and replaced all uses of ChatColor.translateAlternativeColorCodes() to ChatUtil.color for easier readability
    - Marked all issues I saw that were not mentioned on this list as TODO and you should be able to find and fix easily.
    - Fixed any server message's color code typos
    - Changed any "System.out.println()" to either have a //todo next to it or to the new logging system defined in ServerManager.java
    - Replaced all server prefix's in ServerManager.java, use these whenever you need to use a prefix.
     */

    @Override
    public void onEnable() {
        instance = this;
        serverConfigManager.init();
        this.serverConfig = serverConfigManager.getConfig();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(128);
        this.jedisPool = new JedisPool(config);

        registerCommands();

        //this.punishmentManager = new PunishmentManager(MongoClients.create().getDatabase("conquest"));

        registerListeners();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.bossBarManager = new BossBarManager(40, ChatColor.GOLD + "play.conquest-mc.com", ChatColor.GREEN + "More marketing here");
    }

    @Override
    public void onDisable() {
        ServerManager.log("&cShutting down...");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ProfileDefaultListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        //getServer().getPluginManager().registerEvents(new PunishmentListener(punishmentManager), this);
        getServer().getPluginManager().registerEvents(new SignListener(), this);

        getServer().getPluginManager().registerEvents(new CommandPreProcess(), this);
        ServerManager.log("&aSuccessfully registered listeners");
    }

    private void registerCommands() {
        getCommand("gamemode").setExecutor(new GameModeCommand());
        getCommand("punish").setExecutor(new PunishmentCommand(punishmentManager));
        getCommand("ph").setExecutor(new PunishmentHistoryCommand(punishmentManager));
        getCommand("hub").setExecutor(new HubCommand(this));
        getCommand("givecosmetic").setExecutor(new CosmeticCommand(this));
        getCommand("drachma").setExecutor(new DrachmaCommand(this));
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("setrank").setExecutor(new RankCommand(this));

        register(TestCommand.class);
        ServerManager.log("&aSuccessfully registered commands");
    }

    public void applyPermissions(Player player, Rank rank) {
        CorePlayer corePlayer = (CorePlayer) API.getUserManager().findByUniqueId(player.getUniqueId());

        List<String> permissions = corePlayer.getPermissions();

        PermissionAttachment attachment = player.addAttachment(this);


        for (String perm : rank.getPermissions()) {
            if (!permissions.contains(perm)) {
                permissions.add(perm);
            }
        }

        for (String rankName : rank.getInherits()) {
            Rank r = getServerConfig().getRankByName(rankName);
            if (r != null) {
                for (String p : r.getPermissions()) {
                    if (!permissions.contains(p)) {
                        permissions.add(p);
                    }
                }
            }
        }

        for (String perm : permissions) {
            attachment.setPermission(perm, true);
        }
        perms.put(player.getUniqueId(), attachment);
    }

    public void register(Class<? extends CCommand> c) {
        CommandInfo info = c.getAnnotation(CommandInfo.class);
        if (info == null)
            return;

        try {
            commandMap.put(info.name(), c.newInstance());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public CorePlayer getPlayer(UUID uuid) {
        return (CorePlayer) API.getUserManager().findByUniqueId(uuid);
    }
    public boolean isGameServer() {
        return getServer().getPluginManager().isPluginEnabled("ConquestGames");
    }
}
