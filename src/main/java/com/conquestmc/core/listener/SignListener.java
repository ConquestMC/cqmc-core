package com.conquestmc.core.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        for (int i = 0; i < e.getLines().length; i++) {
            e.setLine(i, ChatColor.translateAlternateColorCodes('&', e.getLine(i)));
        }
    }
}
