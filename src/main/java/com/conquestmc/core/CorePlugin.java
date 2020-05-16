package com.conquestmc.core;

import com.conquestmc.core.command.FriendCommand;
import com.conquestmc.core.command.GameModeCommand;
import com.conquestmc.core.command.RankCommand;
import com.conquestmc.core.config.ConfigManager;
import com.conquestmc.core.config.MainConfig;
import com.conquestmc.core.listener.PlayerListener;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.model.Rank;
import com.conquestmc.core.server.ServerManager;
import com.google.common.collect.Maps;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.type.ChestMenu;
import org.jdbi.v3.core.Jdbi;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

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

    @Getter
    private ServerManager serverManager;

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

        this.serverManager = new ServerManager(this);

        registerListeners();
    }

    @Override
    public void onDisable() {

    }

    public Menu getTrialGUI(Rank rank, String punishingName) {
        Menu menu = ChestMenu.builder(6).title(ChatColor.DARK_RED + "Punish " + ChatColor.RED + punishingName).build();
        ItemStack head = new ItemStack(Material.SKULL_ITEM);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner(punishingName);
        meta.setDisplayName(punishingName);
        head.setItemMeta(meta);
        menu.getSlot(5, 1).setItem(head);

        //TODO finish this gui

        return menu;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
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
}
