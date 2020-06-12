package com.conquestmc.core.player;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public class RankManager {

    private Map<String, Rank> ranks = Maps.newHashMap();

    public RankManager() {
        Rank none = new Rank("none");
        Rank explorer = new DonationRank("explorer");
        explorer.setPrefix("&a");
        Rank knight = new DonationRank("knight");
        knight.setPrefix("&b");
        Rank lord = new DonationRank("lord");
        lord.setPrefix("&5");
        Rank king = new DonationRank("king");
        king.setPrefix("&6");
        Rank content = new DonationRank("content");
        content.setPrefix("&d");
        Rank trial = new DonationRank("trial");
        trial.setPrefix("&3&lTRIAL &6");
        Rank mod = new StaffRank("mod", Arrays.asList(trial));
        mod.setPrefix("&2&lMOD &6");
        Rank admin = new StaffRank("admin", Arrays.asList(mod));
        admin.setPrefix("&c&lADMIN &6");
        admin.addPermissions("core.gamemode", "core.gamemode.others", "staff.punish");

        Rank manager = new StaffRank("manager", Arrays.asList(admin));
        manager.setPrefix("&4&lMANAGER &6");
        manager.addPermission("core.setrank");

        Rank dev = new StaffRank("dev", Arrays.asList(manager));
        dev.setPrefix("&5&lDEV &6");
        Rank owner = new StaffRank("owner", Arrays.asList(dev, content, king, lord, knight, explorer, none));
        owner.setPrefix("&4&lOWNER &6");

        ranks.put("none", none);
        ranks.put("explorer", explorer);
        ranks.put("knight", knight);
        ranks.put("lord", lord);
        ranks.put("king", king);
        ranks.put("content", content);
        ranks.put("trial", trial);
        ranks.put("mod", mod);
        ranks.put("admin", admin);
        ranks.put("manager", manager);
        ranks.put("dev", dev);
        ranks.put("owner", owner);
    }

    public Rank getRank(String name) {
        return ranks.get(name);
    }
}
