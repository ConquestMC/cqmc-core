package com.conquestmc.core.model;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public enum Rank {
    NONE("&8"),
    EXPLORER("&a"),
    KNIGHT("&b"),
    LORD("&5"),
    KING("&6"),
    CONTENT_CREATOR("&d"),
    TRIAL("&3&lTRIAL &6"),
    MOD("&2&lMOD &6"),
    ADMIN("&c&lADMIN &6"),
    MANAGER("&4&lMANAGER &6"),
    OWNER("&4&lOWNER &6");
    String prefix;

    Rank(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public static List<Rank> staff() {
        return Arrays.asList(TRIAL, MOD, ADMIN, MANAGER, OWNER);
    }
}
