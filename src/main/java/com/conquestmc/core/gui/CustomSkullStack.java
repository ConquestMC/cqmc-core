package com.conquestmc.core.gui;

import com.conquestmc.core.util.ChatUtil;
import com.conquestmc.core.util.SkullCreator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class CustomSkullStack extends CustomItemStack {

    private String url;
    private String owner;
    private String displayName;

    public CustomSkullStack() {

    }

    public CustomSkullStack withUrl(String url) {
        this.url = url;
        return this;
    }
    public CustomSkullStack withDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public CustomSkullStack withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public ItemStack get() {
        ItemStack skull = null;
        if (owner != null) {
            skull =  SkullCreator.itemFromUuid(UUID.fromString(owner));
        }
        if (url != null) {
            skull = SkullCreator.itemFromUrl(url);
        }

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName(getName());
        meta.setLore(getLore());
        skull.setItemMeta(meta);
        return skull;
    }
}
