package com.conquestmc.core.server;

import com.conquestmc.core.util.ChatUtil;
import org.bukkit.Bukkit;

public class ServerManager {
    public static final String SERVER_PREFIX = ChatUtil.color("&c&lConquest&e&lMC &4» &r");
    public static final String STAFF_PREFIX = ChatUtil.color("&b&lStaff &4» &r");
    public static final String FRIENDS_PREFIX = ChatUtil.color("&a&lFriends &4» &r");
    public static final String SOCIALSPY_PREFIX = ChatUtil.color("&7SocialSpy &8» &r");
    public static final String COMMANDSPY_PREFIX = ChatUtil.color("&7CommandSpy &8» &r");
    public static final String VANISH_PREFIX = ChatUtil.color("&d&lVanish &4» &r");
    public static final String PUNISH_PREFIX = ChatUtil.color("&c&lConquest&e&lMC &c&lPunish &4&l» &r");
    public static final String SENDER_INVALID = SERVER_PREFIX + ChatUtil.color("&cSorry, this command is on executable by players");
    public static final String PLAYER_NO_PERMISSION = SERVER_PREFIX + ChatUtil.color("&cSorry, you don't have permission to do this");

    public static void log(String... log) {
        if (log.length == 1) {
            Bukkit.getConsoleSender().sendMessage(SERVER_PREFIX + ChatUtil.color(log[0]));
            return;
        }
        for (String args : log) Bukkit.getConsoleSender().sendMessage(ChatUtil.color(args));

    }
}
