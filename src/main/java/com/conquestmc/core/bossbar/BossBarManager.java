package com.conquestmc.core.bossbar;

import com.conquestmc.core.CorePlugin;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class BossBarManager {

    private BossBar bar;
    private String[] messages;
    private int messageLength = 20;
    int messageIndex = 0;

    public BossBarManager(int messageLength, String... messages) {
        this.messages = messages;
        this.messageLength = messageLength;
        this.bar = determineBar();
        if (messages.length > 1) {
            new BossBarTask(this).runTaskTimer(CorePlugin.getInstance(), 0L, messageLength);
        }
    }

    public BossBarManager(String bossBarText) {
        this.messages = new String[]{bossBarText};
        this.bar = determineBar();
    }

    public BossBar getBar() {
        return bar;
    }

    private BossBar determineBar() {
        String version = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        System.out.println("VER: " + version);

        if (!version.contains("1_8")) {
            return new BossBar1_13(messages[0]);
        }
        else {
            return new BossBar1_8(messages[0]);
        }
    }

    public void removeBar(Player player) {
        getBar().remove(player);
    }

    public int getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
    }

    public String[] getMessages() {
        return messages;
    }


    public void showBossBar(Player player) {
        getBar().send(player);
    }
}
