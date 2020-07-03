package com.conquestmc.core.gui;

import com.conquestmc.core.gui.menu.ItemMenu;
import com.conquestmc.core.gui.menu.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * @author Ethan Borawski
 */
public abstract class ItemGUI {

    private ItemMenu itemMenu;

    private JavaPlugin instance;

    public ItemGUI(JavaPlugin instance, Player player) {
        this(instance, null, player);
    }

    public ItemGUI(JavaPlugin instance, ItemGUI parent, Player p) {
        if (parent == null) {
            itemMenu = new ItemMenu(instance, getName(), p, 36);
        } else {
            itemMenu = new ItemMenu(instance, parent.getItemMenu(), getName(), p, 36);
        }

        //this.onlineUser = user;
        itemMenu.setCloseOnClick(this.isCloseOnClick());
        this.instance = instance;

        this.registerItems();
    }

    public ItemGUI(JavaPlugin instance, ItemGUI parent, Player p, int size) {
        if (parent == null) {
            itemMenu = new ItemMenu(instance, getName(), p, size);
        } else {
            itemMenu = new ItemMenu(instance, parent.getItemMenu(), getName(), p, size);
        }

        itemMenu.setCloseOnClick(this.isCloseOnClick());
        this.instance = instance;

        this.registerItems();
    }

    public JavaPlugin getPlugin() {
        return instance;
    }

    public void set(int slot, MenuItem item) {
        this.itemMenu.addItem(slot, item);
    }

    public void set(int x, int y, MenuItem item) {
        this.itemMenu.addItem(x, y, item);
    }

    public CustomItemStack createFast(Material m, int amount, String name, String... lore) {
        return this.createFast(m, amount, 0, name, lore);
    }

    public CustomItemStack createFast(Material m, int amount, int data, String name, String... lore) {
        ItemStack is = new ItemStack(m, amount, (short) data);
        ItemMeta meta = is.getItemMeta();

        if (name != null) {
            meta.setDisplayName(name);
        }

        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }

        is.setItemMeta(meta);
        return new CustomItemStack(is);
    }

    public Player getPlayer() {
        return this.itemMenu.getPlayer();
    }

    public void refresh() {
        this.itemMenu.update();
    }

    public ItemMenu getItemMenu() {
        return this.itemMenu;
    }

    public void show() {
        this.itemMenu.show();
    }

    public void close() {
        this.itemMenu.close();
    }

    public abstract String getName();

    public abstract boolean isCloseOnClick();

    public abstract void registerItems();

    public void onClose(Runnable r) {
        this.itemMenu.onClose(r);
    }

}
