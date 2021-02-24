package com.conquestmc.core.listener;

import com.conquestmc.core.util.ChatUtil;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileDefaultListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();
        CorePlayer corePlayer = (CorePlayer) API.getUserManager().findByUniqueId(pl.getUniqueId());
        corePlayer.update();

        FProfile main = corePlayer.getProfile("main");

        if (main == null) {
            main = new FProfile("main", Maps.newHashMap());
            corePlayer.getAllProfiles().add(main);
        }

        main.addDefault("lastLogin", ChatUtil.formatDate.format(new Date()));
        main.addDefault("firstjoin", ChatUtil.formatDate.format(new Date()));
        main.addDefault("playtime", (long)0);
        main.addDefault("friends", Lists.newArrayList());
        main.addDefault("friendsList", Lists.newArrayList());
        main.addDefault("outgoingFriendRequests", Lists.newArrayList());
        main.addDefault("incomingFriendRequests", Lists.newArrayList());
        main.addDefault("drachma", 0.0);
        main.addDefault("points", 0.0);
        main.addDefault("ignoredPlayers", Lists.newArrayList());
        main.addDefault("rank", "none");
        main.addDefault("kills", 0);
        main.addDefault("deaths", 0);
        main.addDefault("wins", 0);
        main.addDefault("loses", 0);
        main.addDefault("kd", 0.0);
        main.addDefault("winRatio", 0.0);
        main.addDefault("topThree", 0);
        main.addDefault("topTen", 0);
        main.addDefault("gamesParticipated", 0);
        main.addDefault("gamesDNF", 0);
        main.addDefault("chestsLooted", 0);
        main.addDefault("currentWinStreak", 0);
        main.addDefault("longestWinStreak", 0);
        main.addDefault("averagePlacement", 0);
        main.addDefault("currentKillStreak", 0);
        main.addDefault("highestKillStreak", 0);
        main.addDefault("combatLog", 0);
        main.addDefault("suicides", 0);
        main.addDefault("lastLogin", ChatUtil.formatDate.format(new Date()));
        main.addDefault("firstJoin", ChatUtil.formatDate.format(new Date()));
        main.addDefault("playtime", (long) 0);
        main.addDefault("ignoredPlayers", new ArrayList<String>());
        main.addDefault("drachmaLifetimeEarnings", 0);
        main.addDefault("drachmaSeasonEarnings", 0);
        main.addDefault("drachmaDailyEarnings", 0);
        main.addDefault("drachmaSpent", 0);
        main.addDefault("trophiesEarned", 0);
        main.addDefault("pointsAwarded", 0);
        main.addDefault("pointsDeducted", 0);
        main.addDefault("pointsLargestDeduction", 0);
        main.addDefault("pointsLargestAward", 0);
        main.addDefault("unlockedCosmetics", Lists.newArrayList());
        main.addDefault("nameColor", "&7");
    }
}
