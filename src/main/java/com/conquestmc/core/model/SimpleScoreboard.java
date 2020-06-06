package com.conquestmc.core.model;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;


import java.util.List;

public class SimpleScoreboard {

    private Scoreboard scoreboard;

    private String title;
    private List<String> scores;
    private List<Team> teams;

    public SimpleScoreboard(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.scores = Lists.newArrayList();
        this.teams = Lists.newArrayList();

        Objective obj = this.scoreboard.registerNewObjective("test", "dummy");
        obj.setDisplayName(title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void addLine(String text) {
        this.scores.add(text);
    }
    public void blankLine() {
        addLine(" ");
    }

    public void send(Player player) {
        int start = scores.size();
        for (String text : scores) {
            this.scoreboard.getObjective("test").getScore(text).setScore(start);
            start -= 1;
        }
        player.setScoreboard(scoreboard);
    }
}