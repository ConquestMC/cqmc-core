package com.conquestmc.core.gui;

import com.conquestmc.core.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Ethan Borawski
 */
public class CustomItemStack {

    private static final String CustomItemStack_META_KEY = "CustomItemStackMeta";

    private String name;
    private final ArrayList<String> lore;
    private Material material;
    private short data;
    private int size;
    private HashMap<Enchantment, Integer> enchantments;
    private ArrayList<ItemFlag> flags;

    public CustomItemStack() {
        this.name = "";
        this.lore = new ArrayList<String>();
        this.material = Material.DIAMOND;
        this.data = 0;
        this.size = 1;
        this.enchantments = new HashMap<Enchantment, Integer>();
        this.flags = new ArrayList<ItemFlag>();
    }

    public CustomItemStack(ItemStack is) {
        this.name = null;
        this.material = is.getType();
        this.data = is.getDurability();
        this.size = is.getAmount();
        this.enchantments = new HashMap();
        this.enchantments.putAll(is.getEnchantments());
        this.flags = new ArrayList();

        ItemMeta meta = is.getItemMeta();
        if (meta == null) {
            this.lore = new ArrayList();
            return;
        }

        this.flags.addAll(meta.getItemFlags());

        if (meta.hasDisplayName()) {
            this.name = meta.getDisplayName();

        }

        if (meta.hasLore()) {
            this.lore = (ArrayList) meta.getLore();

        } else {
            this.lore = new ArrayList();
        }
    }

    public CustomItemStack(final JSONObject dbo) throws IllegalArgumentException {
        this.name = "";
        this.lore = new ArrayList<>();
        this.material = Material.DIAMOND;
        this.data = 0;
        this.size = 1;
        this.enchantments = new HashMap<>();
        this.flags = new ArrayList<>();

        try {
            if (dbo.containsKey("name")) {
                setName(ChatUtil.color((String) dbo.get("name")));
            }

            if (dbo.containsKey("typeid")) {
                setMaterial(Material.getMaterial((String) dbo.get("typeid")));
            }

            if (dbo.containsKey("data")) {
                setData((Byte) dbo.get("data"));
            }

            if (dbo.containsKey("amount")) {
                setSize((Integer) dbo.get("amount"));
            }

            if (dbo.containsKey("lore")) {
                for (Object loredbo : (JSONArray) dbo.get("lore")) {
                    addLore(ChatUtil.color((String) loredbo));
                }
            }

            if (dbo.containsKey("enchantments")) {
                for (Object edbo : (JSONArray) dbo.get("enchantments")) {
                    addEnchantment(
                            Enchantment.getByName(((String) ((JSONObject) edbo).get("name")).toUpperCase()),
                            (Integer) ((JSONObject) edbo).get("level")
                    );
                }
            }

            if (dbo.containsKey("flags")) {
                for (Object flagdbo : (JSONArray) dbo.get("flags")) {
                    addItemFlag(ItemFlag.valueOf(((String) flagdbo).toUpperCase()));
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public JSONObject toJSON() {
        return new JSONObject() {{
            this.put("name", name);
            this.put("lore", lore.stream().collect(Collectors.toCollection((Supplier<JSONArray>) new Supplier<JSONArray>() {
                @Override
                public JSONArray get() {
                    return new JSONArray();
                }
            })));
            this.put("typeid", material.getId());
            this.put("data", data);
            this.put("amount", size);
            this.put("flags", new JSONArray() {{
                for (ItemFlag flag : flags) {
                    add(flag.name());
                }
            }});

            this.put("enchantments", new JSONArray() {{
                enchantments.forEach((enchantment, integer) -> {
                    put("name", enchantment.getName());
                    put("level", enchantments.get(enchantment));
                });
            }});
        }};
    }

    public CustomItemStack clone() {

        CustomItemStack cis = new CustomItemStack();

        cis.setName(name);
        cis.setMaterial(material);
        cis.setData(data);
        cis.setSize(size);

        for (String l : lore) {
            cis.addLore(l);
        }

        for (Enchantment e : enchantments.keySet()) {
            cis.addEnchantment(e, enchantments.get(e));
        }

        for (ItemFlag _if : flags) {
            cis.addItemFlag(_if);
        }

        return cis;
    }

    public CustomItemStack addItemFlag(ItemFlag _if) {
        if (!flags.contains(_if)) {
            flags.add(_if);
        }
        return this;
    }

    public CustomItemStack addEnchantment(Enchantment e, int level) {
        enchantments.put(e, level);
        return this;
    }

    public boolean equals(ItemStack is) {
        CustomItemStack comp = this;
        return (comp.equals(is));
    }

    public CustomItemStack setName(String name) {
        this.name = name.replace("&", ChatColor.COLOR_CHAR + "");
        return this;
    }

    public CustomItemStack addLore(String line) {
        lore.add(line.replace("&", ChatColor.COLOR_CHAR + ""));
        return this;
    }

    public CustomItemStack setMaterial(Material mat) {
        material = mat;
        return this;
    }

    public CustomItemStack setData(short data) {
        this.data = data;
        return this;
    }

    public CustomItemStack setSize(int size) {
        this.size = size;
        return this;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public short getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<String> getLore() {
        return lore;
    }

    public HashMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public ArrayList<ItemFlag> getFlags() {
        return flags;
    }

    public ItemStack get() {
        ItemStack is = new ItemStack(material, size);
        is.setDurability((short) data);

        final ItemMeta im = is.getItemMeta();
        if (name != null && !name.isEmpty()) {
            im.setDisplayName(name);
        }

        for (ItemFlag flag : flags) {
            im.addItemFlags(flag);
        }

        ArrayList<String> lore_wmeta = (ArrayList<String>) lore.clone();
        im.setLore(lore_wmeta);

        is.setItemMeta(im);
        is.addUnsafeEnchantments(enchantments);

        return is;
    }


}
