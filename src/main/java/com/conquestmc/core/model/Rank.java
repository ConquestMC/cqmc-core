package com.conquestmc.core.model;

import org.bukkit.ChatColor;

public enum Rank {
    NONE("&8"),
    COMMONER("&a"),
    KNIGHT("&b"),
    LORD("&5"),
    KING("&6"),
    CONTENT_CREATOR("&9"),
    MOD("&d&l"),
    ADMIN("&c&l"),
    OWNER("&4");
    String prefix;

    Rank(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }
}
