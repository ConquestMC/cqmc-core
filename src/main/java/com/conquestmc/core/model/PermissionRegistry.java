package com.conquestmc.core.model;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PermissionRegistry {
    OWNER(
            "core.gamemode",
            "core.gamemode.others",
            "core.setrank",
            "core.build"
    ),
    MANAGER("core.gamemode",
            "core.gamemode.others",
            "core.setrank",
            "staff.punish");

    private List<String> perms;
    PermissionRegistry(String... perms) {
        this.perms = Arrays.asList(perms);
    }

    public List<String> getPermissions() {
        return perms;
    }
}
