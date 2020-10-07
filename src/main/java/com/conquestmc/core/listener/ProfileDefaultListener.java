package com.conquestmc.core.listener;

import com.conquestmc.foundation.API;
import com.conquestmc.foundation.CorePlayer;
import com.conquestmc.foundation.player.FProfile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class ProfileDefaultListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();
        CorePlayer corePlayer = (CorePlayer) API.getUserManager().findByUniqueId(pl.getUniqueId());

        FProfile coreProfile = corePlayer.getProfile("core");
        FProfile permissions = corePlayer.getProfile("permissions");
        FProfile cosmeticProfile = corePlayer.getProfile("cosmetics");
        FProfile friendsProfile = corePlayer.getProfile("friends");

        if (friendsProfile == null) {
            friendsProfile = new FProfile("friends", Maps.newHashMap());
        }

        if (coreProfile == null) {
            coreProfile = new FProfile("core", Maps.newHashMap());
            coreProfile.set("drachma", 0.0);
            coreProfile.set("points", 0.0);
            coreProfile.set("friends", new ArrayList<String>());
            corePlayer.getAllProfiles().add(coreProfile);
        }

        if (permissions == null) {
            permissions = new FProfile("permissions", Maps.newHashMap());
            permissions.set("rank", "none");
            corePlayer.getAllProfiles().add(permissions);
        }
        FProfile gameProfile = corePlayer.getProfile("game");

        if (gameProfile == null) {
            gameProfile = new FProfile("game", Maps.newHashMap());
            gameProfile.set("kills", (int)0);
            gameProfile.set("deaths", (int)0);
            gameProfile.set("wins", (int)0);
            corePlayer.getAllProfiles().add(gameProfile);
        }
        if (cosmeticProfile == null) {
            cosmeticProfile = new FProfile("cosmetics", Maps.newHashMap());
            cosmeticProfile.set("unlockedTrails", new ArrayList<>());
            cosmeticProfile.set("unlockedGadgets", new ArrayList<>());
            cosmeticProfile.set("unlockedCages", new ArrayList<>());
            cosmeticProfile.set("nameColor", "&7");
            corePlayer.getAllProfiles().add(cosmeticProfile);
        }

        friendsProfile.addDefault("friendsList", Lists.newArrayList());
        friendsProfile.addDefault("outgoingRequests", Lists.newArrayList());
        friendsProfile.addDefault("incomingRequests", Lists.newArrayList());
    }
}
