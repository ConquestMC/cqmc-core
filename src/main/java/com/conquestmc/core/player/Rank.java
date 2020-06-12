package com.conquestmc.core.player;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class Rank {

    private String name;
    private List<Rank> inherits = Lists.newArrayList();
    private List<String> permissions = Lists.newArrayList();

    private String prefix = "&8";

    public Rank(String name) {
        this.name = name;
    }

    public Rank(String name, List<Rank> inherits) {
        this.name = name;
        this.inherits = inherits;
        for (Rank rank : inherits) {
            permissions.addAll(rank.getPermissions());
        }
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void addPermission(String perm) {
        this.permissions.add(perm);
    }
    public void addPermissions(String... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public List<Rank> getInherits() {
        return inherits;
    }
}
