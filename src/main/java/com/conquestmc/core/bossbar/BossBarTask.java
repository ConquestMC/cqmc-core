package com.conquestmc.core.bossbar;

import org.bukkit.scheduler.BukkitRunnable;

public class BossBarTask extends BukkitRunnable {

    private BossBarManager bossBarManager;

    public BossBarTask(BossBarManager bossBarManager) {
        this.bossBarManager = bossBarManager;
    }

    @Override
    public void run() {
        if (bossBarManager.getMessageIndex() == bossBarManager.getMessages().length) {
            bossBarManager.setMessageIndex(0);
        }
        bossBarManager.getBar().setText(bossBarManager.getMessages()[bossBarManager.getMessageIndex()]);
        bossBarManager.setMessageIndex(bossBarManager.getMessageIndex()+1);
    }
}
