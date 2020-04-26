package com.conquestmc.core;

import com.conquestmc.core.command.GameModeCommand;
import com.conquestmc.core.command.RankCommand;
import com.conquestmc.core.config.ConfigManager;
import com.conquestmc.core.config.MainConfig;
import com.conquestmc.core.dao.PlayerDao;
import com.conquestmc.core.listener.PlayerListener;
import com.conquestmc.core.model.ConquestPlayer;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class CorePlugin extends JavaPlugin {

    private ConfigManager serverConfigManager = new ConfigManager(getDataFolder().getName(), "config.json", MainConfig.class);

    @Getter
    private Map<UUID, ConquestPlayer> players = Maps.newHashMap();

    @Getter
    private MainConfig serverConfig;

    @Getter
    private PlayerDao playerDao;

    @Override
    public void onEnable() {
        serverConfigManager.init();
        this.serverConfig = (MainConfig) serverConfigManager.getConfig();

        Properties props = new Properties();
        props.put("user", "root");
        props.put("password", "t967vsTzA3");
        Jdbi jdbi = Jdbi.create("jdbc:mysql://localhost:3306/test", props);
        jdbi.installPlugin(new SqlObjectPlugin());

        this.playerDao = jdbi.onDemand(PlayerDao.class);
        this.playerDao.createTables();

        getCommand("gamemode").setExecutor(new GameModeCommand());
        getCommand("setrank").setExecutor(new RankCommand(this));

        registerListeners();
    }

    @Override
    public void onDisable() {

    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public ConquestPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public ConquestPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }
}
