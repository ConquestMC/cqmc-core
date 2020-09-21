package com.conquestmc.core;

import com.conquestmc.core.command.*;
import com.conquestmc.core.config.ConfigManager;
import com.conquestmc.core.config.MainConfig;
import com.conquestmc.core.friends.FriendListener;
import com.conquestmc.core.friends.FriendRequestListener;
import com.conquestmc.core.listener.PlayerListener;
import com.conquestmc.core.listener.RedisLockListener;
import com.conquestmc.core.listener.SignListener;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.player.PlayerManager;
import com.conquestmc.core.player.RankManager;
import com.conquestmc.core.punishments.PunishmentCommand;
import com.conquestmc.core.punishments.PunishmentHistoryCommand;
import com.conquestmc.core.punishments.PunishmentListener;
import com.conquestmc.core.punishments.PunishmentManager;
import com.conquestmc.core.server.ServerManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

public class CorePlugin extends JavaPlugin {

    @Getter
    private ConfigManager<MainConfig> serverConfigManager = new ConfigManager<>(this, "config.json", MainConfig.class);

    @Getter
    private MainConfig serverConfig;

    @Getter
    private static CorePlugin instance;

    @Getter
    private JedisPool jedisPool;

    private MongoClient mongoClient;
    private MongoDatabase playerDatabase;

    @Getter
    private MongoCollection<Document> playerCollection;

    @Getter
    private Map<UUID, PermissionAttachment> perms = Maps.newHashMap();

    private PunishmentManager punishmentManager;

    @Getter
    private PlayerManager playerManager;

    @Getter
    private RankManager rankManager;

    List<String> onlinePlayers = Lists.newArrayList();

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

        this.mongoClient = MongoClients.create();
        this.playerDatabase = mongoClient.getDatabase("conquest");
        this.playerCollection = playerDatabase.getCollection("players");
        this.punishmentManager = new PunishmentManager(playerDatabase);

        this.rankManager = new RankManager();
        this.playerManager = new PlayerManager(playerCollection);

        registerCommands();

        new Thread(() -> jedisPool.getResource().subscribe(new RedisLockListener(this), "redis.lock"), "redis").start();

        registerListeners();
        registerChannelListeners();
    }

    @Override
    public void onDisable() {
        ServerManager.log("&cShutting down...");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new FriendListener(), this);
        getServer().getPluginManager().registerEvents(new PunishmentListener(punishmentManager), this);
        getServer().getPluginManager().registerEvents(new SignListener(), this);
        ServerManager.log("&aSuccessfully registered listeners");
    }

    private void registerCommands() {
        getCommand("gamemode").setExecutor(new GameModeCommand());
        getCommand("giverank").setExecutor(new RankCommand(this));
        getCommand("friend").setExecutor(new FriendCommand(this));
        getCommand("punish").setExecutor(new PunishmentCommand(punishmentManager));
        getCommand("ph").setExecutor(new PunishmentHistoryCommand(punishmentManager));
        getCommand("hub").setExecutor(new HubCommand(this));
        getCommand("demote").setExecutor(new DemoteCommand());
        getCommand("givecosmetic").setExecutor(new CosmeticCommand(this));
        getCommand("drachma").setExecutor(new DrachmaCommand(this));
        getCommand("speed").setExecutor(new SpeedCommand());
        ServerManager.log("&aSuccessfully registered commands");
    }

    public ConquestPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public ConquestPlayer getPlayer(UUID uuid) {
        return playerManager.getPlayers().get(uuid);
    }

    public List<String> getOnlinePlayerNames() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try (Jedis j = getJedisPool().getResource()) {
                onlinePlayers = j.lrange("players", 0, -1);
            }
        });
        return onlinePlayers;
    }

    public void registerChannelListeners() {
        new Thread(() -> {
            try (Jedis listener = jedisPool.getResource()) {
                listener.subscribe(new FriendRequestListener(), "friend.request");
            }
        }, "redisListener").start();
    }

    public void logPlayer(Player player) {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try (Jedis j = getJedisPool().getResource()) {
                j.lpush("players", player.getName());
            }
        });
    }

    public void remPlayer(Player player) {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try (Jedis j = getJedisPool().getResource()) {
                j.lrem("players", 1, player.getName());
            }
        });
    }

    public CompletableFuture<Document> findPlayer(UUID uuid) {
        CompletableFuture<Document> promise = new CompletableFuture<>();

        getPlayerCollection().find(eq("uuid", uuid.toString())).first((d, throwable) -> {
            if (throwable != null) {
                System.err.println(throwable.toString());
            } else {
                promise.complete(d);
            }
        });
        return promise;
    }

    public boolean isPlayerOnNetwork(String name) {
        return getOnlinePlayerNames().contains(name);
    }

    public boolean isGameServer() {
        return getServer().getPluginManager().isPluginEnabled("ConquestGames");
    }
}
