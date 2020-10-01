package com.conquestmc.core.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class BossBar1_13 extends BossBar {

    private org.bukkit.boss.BossBar bar;

    public BossBar1_13(String text) {
        super(text);
        bar = Bukkit.createBossBar(text, BarColor.PURPLE, BarStyle.SOLID);
    }

    @Override
    public void send(Player player) {

        bar.addPlayer(player);
    }

    @Override
    public void remove(Player player) {
        bar.removePlayer(player);
    }
}
