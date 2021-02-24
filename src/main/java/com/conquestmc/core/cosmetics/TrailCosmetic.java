package com.conquestmc.core.cosmetics;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TrailCosmetic implements Cosmetic, Listener {

    private String name;
    private Particle particle;

    public TrailCosmetic(String name, Particle particle) {
        this.name = name;
        this.particle = particle;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void play(Player player) {
        player.getLocation().getWorld().spawnParticle(particle, player.getLocation(), 0);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getX() != event.getTo().getX() && event.getFrom().getZ() != event.getTo().getZ()) {
            play(event.getPlayer());
        }
    }
}
