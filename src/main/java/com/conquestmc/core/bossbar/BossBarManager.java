package com.conquestmc.core.bossbar;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class BossBarManager {

    private BossBar bar;
    private String bossBarText;

    public BossBarManager(String bossBarText) {
        this.bossBarText = bossBarText;
        this.bar = determineBar();
    }

    public BossBar getBar() {
        return bar;
    }

    private BossBar determineBar() {
        String version = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        System.out.println("VER: " + version);

        if (!version.contains("1_8")) {
            return new BossBar1_13(bossBarText);
        }
        else {
            return new BossBar1_8(bossBarText);
        }
    }

    public void removeBar(Player player) {
        getBar().remove(player);
    }

    public void showBossBar(Player player) {
        getBar().send(player);
    }
}
