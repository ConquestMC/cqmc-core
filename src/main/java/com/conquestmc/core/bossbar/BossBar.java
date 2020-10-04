package com.conquestmc.core.bossbar;

import org.bukkit.entity.Player;

public abstract class BossBar {

    private String text;

    public BossBar(String text) {
        this.text = text;
    }

    public abstract void send(Player player);
    public abstract void remove(Player player);

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
