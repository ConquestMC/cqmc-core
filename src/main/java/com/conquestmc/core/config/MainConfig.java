package com.conquestmc.core.config;

import com.conquestmc.core.model.Rank;
import lombok.Data;

import java.util.List;

@Data
public class MainConfig {
    private final List<String> bannedWords;
    private List<AccessCode> accessCodes;
    private List<Rank> ranks;


    public Rank getRankByName(String name) {
        for (Rank rank : ranks) {
            if (rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }
        return null;
    }
}
