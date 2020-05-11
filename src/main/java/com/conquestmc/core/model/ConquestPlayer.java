package com.conquestmc.core.model;

import com.google.common.collect.Lists;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class ConquestPlayer {

    private UUID uuid;
    private String knownName;

    private Rank rank;
    private int coins;
    private long score;

    private List<UUID> friends = Lists.newArrayList();

    private Set<Statistic> statistics = new HashSet<>();

    @ConstructorProperties({"uuid", "knownName", "rankName", "coins", "conquest_points"})
    public ConquestPlayer(String uuid, String knownName, String rankName, int coins, long conquest_points) {
        this.uuid = UUID.fromString(uuid);
        this.knownName = knownName;
        this.rank = Rank.valueOf(rankName);
        this.coins = coins;
        this.score = conquest_points;
    }

    public ConquestPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.knownName = name;
        this.coins = 0;
        this.score = 0;
        this.rank = Rank.NONE;
    }

    public Statistic getStatistic(String name) {
        for (Statistic statistic : statistics) {
            if (statistic.getName().equalsIgnoreCase(name)) {
                return statistic;
            }
        }
        return null;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
