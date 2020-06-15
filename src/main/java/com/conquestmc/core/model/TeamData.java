package com.conquestmc.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@AllArgsConstructor
@Getter
@Setter
public class TeamData {

    private String teamName;
    private String prefix;
    private String entry;
    private String suffix;
    private String encoding;

    public void clearEntries(Scoreboard scoreboard) {
        Team team = scoreboard.getTeam(teamName);
        if (team != null)
            for (String entry : team.getEntries()) {
                team.removeEntry(entry);
            }
    }
}
