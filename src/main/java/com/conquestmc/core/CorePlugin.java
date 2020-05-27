package com.conquestmc.core;

import com.conquestmc.core.command.FriendCommand;
import com.conquestmc.core.command.GameModeCommand;
import com.conquestmc.core.command.RankCommand;
import com.conquestmc.core.config.ConfigManager;
import com.conquestmc.core.config.MainConfig;
import com.conquestmc.core.friends.FriendListener;
import com.conquestmc.core.friends.FriendRequestListener;
import com.conquestmc.core.listener.PlayerListener;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.model.Rank;
import com.conquestmc.core.util.ItemBuilder;
import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.type.ChestMenu;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class CorePlugin extends JavaPlugin {

    @Getter
    private ConfigManager serverConfigManager = new ConfigManager(getDataFolder().getName(), "config.json", MainConfig.class);

    @Getter
    private Map<UUID, ConquestPlayer> players = Maps.newHashMap();

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

    @Override
    public void onEnable() {
        instance = this;
        serverConfigManager.init();
        this.serverConfig = (MainConfig) serverConfigManager.getConfig();

        this.jedisPool = new JedisPool();

        this.mongoClient = new MongoClient();
        this.playerDatabase = mongoClient.getDatabase("conquest");
        this.playerCollection = playerDatabase.getCollection("players");

        getCommand("gamemode").setExecutor(new GameModeCommand());
        getCommand("setrank").setExecutor(new RankCommand(this));
        getCommand("friend").setExecutor(new FriendCommand(this));

        registerListeners();
        registerChannelListeners();
    }

    @Override
    public void onDisable() {

    }

    public Menu getTrialGUI(Rank rank, String punishingName) {
        Menu menu = ChestMenu.builder(6).title(ChatColor.DARK_RED + "Punish " + ChatColor.RED + punishingName).build();
        ItemStack head = new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(punishingName)
                .setName(punishingName)
                .toItemStack();

        menu.getSlot(5, 1).setItem(head);
        {

        }
        //TODO finish this gui

        return menu;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new FriendListener(), this);
    }

    private void registerCommands() {
        getCommand("friends").setExecutor(new FriendCommand(this));
    }

    public ConquestPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public ConquestPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
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
        Document doc = getPlayerCollection().find(eq("_id", uuid.toString())).first();

        if (doc == null) {
            return null;
        }
        System.out.println("DOC: " + doc.toJson());
        return doc;
    }

    public boolean isPlayerOnNetwork(String name) {
        return getOnlinePlayerNames().contains(name);
    }
}
