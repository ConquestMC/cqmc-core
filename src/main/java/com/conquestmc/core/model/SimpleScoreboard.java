package com.conquestmc.core.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Getter
public class SimpleScoreboard {
    private Scoreboard scoreboard;
    private Objective objective;
    private List<TeamData> scores = new ArrayList<>(16);
    private Queue<Runnable> processQueue = Lists.newLinkedList();

    public SimpleScoreboard(String displayName) {
        this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("main", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(displayName);
    }

    public List<String> getLines() {
        List<String> lines = Lists.newArrayList();
        for (TeamData data : scores) {
            lines.add(data.getPrefix() + data.getEntry() + data.getSuffix());
        }
        return lines;
    }

    public SimpleScoreboard addLine(String text) {
        if (scores.size() <= 16) {
            int newScore = scores.size();
            for (TeamData data : scores) {
                Score score = objective.getScore(data.getEntry());
                score.setScore(newScore--);
            }

            Team team = scoreboard.registerNewTeam(UUID.randomUUID().toString().substring(0, 8));
            String[] components = encodeLine(scores.size(), text, null);
            team.setPrefix(components[0]);
            team.addEntry(components[1]);
            team.setSuffix(components[2]);
            scores.add(new TeamData(team.getName(), components[0], components[1], components[2], components[3]));
            objective.getScore(components[1]).setScore(0);
        }
        return this;

    }

    public SimpleScoreboard addBlankLine() {
        addLine(" ");
        return this;
    }

    /**
     * Sets a line on the scoreboard. The first line on the
     * scoreboard has line number 0.
     *
     * @param line The line to set.
     * @param text The text to set the line to.
     */
    public void setLine(int line, String text) {
        if (line >= 0 && line < 16) {
            int size = scores.size();
            if (size <= line) {
                for (int i = size; i < line + 1; i++) {
                    addBlankLine();
                }
            }
            TeamData data = scores.get(line);
            Team team = scoreboard.getTeam(data.getTeamName());
            String[] components = encodeLine(line, text, data.getTeamName());
            if (data.getEntry().equals(components[1])) {
                team.setPrefix(components[0]);
                team.setSuffix(components[2]);
            } else {
                scoreboard.resetScores(data.getEntry());
                team.setPrefix(components[0]);
                data.clearEntries(scoreboard);
                data.setEntry(components[1]);
                data.setEncoding(components[3]);
                team.addEntry(components[1]);
                team.setSuffix(components[2]);
                objective.getScore(components[1]).setScore(line);
            }
        }
    }

    /**
     * Removes a line from the scoreboard. All lines below
     * the removal line will get translated up. The first
     * line on the scoreboard has line number 0.
     *
     * @param line The line to remove.
     */
    public void removeLine(int line) {
        if (line >= 0 && line < scores.size()) {
            TeamData data = scores.remove(line);
            scoreboard.resetScores(data.getEntry());
            renumberLines(true, true);
            processQueue.add(() -> scoreboard.resetScores(data.getEntry()));

//            for(int i = scores.size() - 1; i > line; i--) {
//                Score score = objective.getScore(scores.get(i).getEntry());
//                score.setScore(score.getScore() - 1);
//            }
        }
    }

    public void update(boolean resetScores, boolean reencode) {
        while (processQueue.peek() != null) {
            processQueue.poll().run();
        }
        renumberLines(resetScores, reencode);
    }

    /**
     * Sets the scoreboard for a {@link Player}.
     *
     * @param player The player to set the scoreboard for.
     */
    public void setScoreboard(Player player) {
        player.setScoreboard(scoreboard);
    }

    private String[] encodeLine(int index, String line, String teamName) {
        int length = line.length();
        String prefix = line.substring(0, Math.min(16, length));
        String body = length >= 16 ? line.substring(16, Math.min(32, length)) : "";
        String suffix = length >= 32 ? line.substring(32, Math.min(48, length)) : "";

        if (prefix.length() == 16 && prefix.charAt(15) == '§') {
            prefix = prefix.substring(0, 15);
            if (body.length() >= 1) {
                suffix += body.charAt(body.length() - 1);
                body = "§" + body.substring(0, Math.min(15, body.length()));
            }
        }
        if (body.length() == 16 && body.charAt(15) == '§') {
            body = body.substring(0, 15);
            suffix = '§' + suffix;
        }

        String lastColor = ChatColor.getLastColors(body);
        if (lastColor.isEmpty()) {
            lastColor = ChatColor.getLastColors(prefix);
        }
//        if(lastColor.isEmpty()) {
//            lastColor = ChatColor.RESET.toString();
//        }

        if (suffix.isEmpty()) {
            suffix = lastColor + body;
            body = ChatColor.values()[index] + "";
        } else if (body.isEmpty()) {
            body = ChatColor.values()[index] + "";
        }

        String[] bodyComponent = encodeBody(index, body, teamName);
        body = bodyComponent[0];
        suffix = (!suffix.startsWith("§") ? lastColor : "") + bodyComponent[1] + suffix;
        suffix = suffix.substring(0, Math.min(16, suffix.length()));

        // Chat color is encoding
        return new String[]{prefix, body, suffix, bodyComponent[2]};
    }

    // [0] = new body
    // [1] = characters that were removed
    // [2] = encoding value
    private String[] encodeBody(int index, String body, String teamName) {
        String[] components = new String[3];
        String finalString = body;
        if (bodyExists(body, teamName)) {
            finalString = ChatColor.values()[index] + body;
            components[2] = finalString.substring(0, 2);
        } else {
            components[2] = "";
        }
        if (finalString.length() > 16) {
            components[0] = finalString.substring(0, 16);
            components[1] = finalString.substring(16);
        } else {
            components[0] = finalString;
            components[1] = "";
        }

        return components;
    }

    private boolean bodyExists(String body) {
        for (TeamData data : scores) {
            if (data.getEntry().equals(body))
                return true;
        }

        return false;
    }

    private boolean bodyExists(String body, String teamName) {
        for (TeamData data : scores) {
            if (!data.getTeamName().equals(teamName) && data.getEntry().equals(body))
                return true;
        }

        return false;
    }

    private void reencodeLines() {
        int current = 0;
        for (TeamData data : scores) {
            Team team = scoreboard.getTeam(data.getTeamName());
            String line = team.getPrefix() + data.getEntry().replaceFirst(data.getEncoding(), "") + team.getSuffix();
            String[] encoded = encodeLine(scores.size() - current - 1, line, data.getTeamName());
            team.setPrefix(encoded[0]);
            data.clearEntries(scoreboard);
            data.setEntry(encoded[1]);
            team.addEntry(encoded[1]);
            team.setSuffix(encoded[2]);
            current++;
        }
    }

    private void renumberLines(boolean resetScores, boolean reencode) {
        if (resetScores) {
            for (TeamData data : scores) {
                scoreboard.resetScores(data.getEntry());
            }
        }
        if (reencode)
            reencodeLines();
        for (int i = 0; i < scores.size(); i++) {
            Score score = objective.getScore(scores.get(i).getEntry());
            score.setScore(scores.size() - i - 1);
        }
    }

    private ChatColor getNextColor(ChatColor current) {
        if (current.ordinal() + 1 >= ChatColor.values().length)
            return ChatColor.BLACK;

        return ChatColor.values()[current.ordinal() + 1];
    }
}