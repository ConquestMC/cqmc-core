package com.conquestmc.core;

import com.conquestmc.core.command.*;
import com.conquestmc.core.config.ConfigManager;
import com.conquestmc.core.config.MainConfig;
import com.conquestmc.core.friends.FriendListener;
import com.conquestmc.core.friends.FriendRequestListener;
import com.conquestmc.core.listener.PlayerListener;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.player.PlayerManager;
import com.conquestmc.core.player.RankManager;
import com.conquestmc.core.punishments.PunishmentCommand;
import com.conquestmc.core.punishments.PunishmentHistoryCommand;
import com.conquestmc.core.punishments.PunishmentListener;
import com.conquestmc.core.punishments.PunishmentManager;
import com.conquestmc.core.rest.PlayerRestfulService;
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
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    @Override
    public void onEnable() {
        instance = this;
        serverConfigManager.init();
        this.serverConfig = (MainConfig) serverConfigManager.getConfig();

        this.jedisPool = new JedisPool();

        this.mongoClient = MongoClients.create();
        this.playerDatabase = mongoClient.getDatabase("conquest");
        this.playerCollection = playerDatabase.getCollection("players");
        this.punishmentManager = new PunishmentManager(playerDatabase);

        this.rankManager = new RankManager();
        this.playerManager = new PlayerManager(playerCollection);


        getCommand("gamemode").setExecutor(new GameModeCommand());
        getCommand("giverank").setExecutor(new RankCommand(this));
        getCommand("friend").setExecutor(new FriendCommand(this));
        getCommand("punish").setExecutor(new PunishmentCommand(punishmentManager));
        getCommand("ph").setExecutor(new PunishmentHistoryCommand(punishmentManager));
        getCommand("hub").setExecutor(new HubCommand(this));
        getCommand("demote").setExecutor(new DemoteCommand());

        registerListeners();
        registerChannelListeners();
    }

    @Override
    public void onDisable() {

    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new FriendListener(), this);
        getServer().getPluginManager().registerEvents(new PunishmentListener(punishmentManager), this);
    }

    public ConquestPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public ConquestPlayer getPlayer(UUID uuid) {
        return playerManager.getPlayers().get(uuid);
    }

    public List<String> getOnlinePlayerNames() {
        try (Jedis j = getJedisPool().getResource()) {
            List<String> onlinePlayerNames= j.lrange("players", 0, -1);
            return onlinePlayerNames;
        }
    }

    public void registerChannelListeners() {
        new Thread(() -> {
            try (Jedis listener = jedisPool.getResource()) {
                listener.subscribe(new FriendRequestListener(), "friend.request");
            }
        }, "redisListener").start();
    }

    public void logPlayer(ConquestPlayer player) {
        try (Jedis j = getJedisPool().getResource()) {
            j.lpush("players", player.getKnownName());
        }
    }

    public void remPlayer(ConquestPlayer player) {
        try (Jedis j = getJedisPool().getResource()) {
            j.lrem("players", 1, player.getKnownName());
        }
    }

    public Document findPlayer(UUID uuid) {
        CompletableFuture<Document> promise = new CompletableFuture<>();
        getPlayerCollection().find(eq("uuid", uuid.toString())).first((d, throwable) -> {
            promise.complete(d);
        });

        try {
            return promise.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPlayerOnNetwork(String name) {
        return getOnlinePlayerNames().contains(name);
    }
}
