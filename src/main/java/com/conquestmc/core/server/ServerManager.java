package com.conquestmc.core.server;

import com.conquestmc.core.util.ChatUtil;
import org.bukkit.Bukkit;

public class ServerManager {

    public static void log(String... log) {
        if (log.length == 1) {
            Bukkit.getConsoleSender().sendMessage(ServerMessages.SERVER_PREFIX.getPrefix() + ChatUtil.color(log[0]));
            return;
        }
        for (String args : log) Bukkit.getConsoleSender().sendMessage(ChatUtil.color(args));

    }
}
