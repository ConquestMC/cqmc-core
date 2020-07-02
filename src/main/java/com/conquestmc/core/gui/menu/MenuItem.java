package com.conquestmc.core.gui.menu;

import com.conquestmc.core.gui.CustomItemStack;
import com.conquestmc.core.gui.ItemGUI;

/**
 * @author Ethan Borawski
 */
public class MenuItem {

    private CustomItemStack item;
    private Runnable exec;

    public MenuItem(CustomItemStack item, Runnable exec) {
        this.item = item;
        this.exec = exec;
    }

    public CustomItemStack getItem() {
        return item;
    }

    public Runnable getExec() {
        return exec;
    }

    public static class SubMenuItem extends MenuItem {

        public SubMenuItem(CustomItemStack item, final ItemMenu child) {
            super(item, new Runnable() {
                @Override
                public void run() {
                    child.show();
                }
            });
        }

        public SubMenuItem(CustomItemStack item, ItemGUI child) {
            this(item, child.getItemMenu());
        }

    }

    public static class BackMenuItem extends MenuItem {

        public BackMenuItem(CustomItemStack item, final ItemMenu current) {
            super(item, new Runnable() {
                @Override
                public void run() {
                    if (current.hasParent()) {
                        current.getParent().show();
                    }
                }
            });
        }

        public BackMenuItem(CustomItemStack item, ItemGUI current) {
            this(item, current.getItemMenu());
        }

    }

}
