package com.conquestmc.core.model;

import com.google.common.collect.Lists;
import lombok.Data;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.config.RegisterFieldMappers;

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

    public ConquestPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.knownName = name;
    }
}
