package com.conquestmc.core.cosmetics;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemTrailCosmetic extends TrailCosmetic {


    public ItemTrailCosmetic(String name, Particle particle) {
        super(name, particle);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void play(Player player) {
        player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DIAMOND));
    }
}
