package com.conquestmc.core.model;

import java.util.List;

public class Rank {
    private String name;
    private String prefix;
    private List<String> inherits;
    private List<String> permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<String> getInherits() {
        return inherits;
    }

    public void setInherits(List<String> inherits) {
        this.inherits = inherits;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
