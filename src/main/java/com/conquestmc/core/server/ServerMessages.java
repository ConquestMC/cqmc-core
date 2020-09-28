package com.conquestmc.core.server;

import com.conquestmc.core.util.ChatUtil;

public enum ServerMessages {
    SERVER_PREFIX(ChatUtil.color("&c&lConquest&e&lMC &4&l» &r")),
    STAFF_PREFIX(ChatUtil.color("&b&lStaff &4&l» &r")),
    FRIENDS_PREFIX(ChatUtil.color("&a&lFriends &4&l» &r")),
    SOCIALSPY_PREFIX(ChatUtil.color("&7SocialSpy &8» &r")),
    COMMANDSPY_PREFIX(ChatUtil.color("&7CommandSpy &8» &r")),
    VANISH_PREFIX(ChatUtil.color("&d&lVanish &4&l» &r")),
    PUNISH_PREFIX(ChatUtil.color("&c&lConquest&e&lMC &c&lPunish &4&l» &r")),
    SENDER_INVALID(SERVER_PREFIX.getPrefix() + ChatUtil.color("&cSorry, this command is on executable by players")),
    PLAYER_NO_PERMISSION(SERVER_PREFIX.getPrefix() + ChatUtil.color("&cSorry, you don't have permission to do this"));


    private String prefix;

    ServerMessages(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
